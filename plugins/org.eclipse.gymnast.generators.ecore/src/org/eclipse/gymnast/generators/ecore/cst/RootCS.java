package org.eclipse.gymnast.generators.ecore.cst;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.gymnast.generators.ecore.errors.Grammar2EcoreParseWarning;
import org.eclipse.gymnast.generators.ecore.errors.Grammar2EcoreInvalidInput.EmptyTokenRule.GeneralMessage;
import org.eclipse.gymnast.generators.ecore.errors.Grammar2EcoreInvalidInput.EmptyTokenRule.MissingGrammarOption;
import org.eclipse.gymnast.generators.ecore.errors.Grammar2EcoreInvalidInput.EmptyTokenRule.MultipleEntryRules;
import org.eclipse.gymnast.generators.ecore.errors.Grammar2EcoreInvalidInput.EmptyTokenRule.RuleWithNameDuplicate;
import org.eclipse.gymnast.generators.ecore.walker.GymnastWalker;
import org.eclipse.gymnast.runtime.core.parser.ParseContext;
import org.eclipse.gymnast.runtime.core.parser.ParseWarning;

/**
 * @author Miguel Garcia, http://www.sts.tu-harburg.de/~mi.garcia/
 * 
 */
public class RootCS {

	public final static Map<String, String> knownBuiltInTokens = new HashMap<String, String>();

	static {
		knownBuiltInTokens.put("LCURLY", "{");
		knownBuiltInTokens.put("RCURLY", "}");
		knownBuiltInTokens.put("LSQUARE", "[");
		knownBuiltInTokens.put("RSQUARE", "]");
		knownBuiltInTokens.put("LPAREN", "(");
		knownBuiltInTokens.put("RPAREN", ")");
		knownBuiltInTokens.put("DOT", ".");
		knownBuiltInTokens.put("COMMA", ",");
		knownBuiltInTokens.put("COLON", ":");
		knownBuiltInTokens.put("SEMI", ";");
		knownBuiltInTokens.put("STAR", "*");
		knownBuiltInTokens.put("PLUS", "+");
		knownBuiltInTokens.put("MINUS", "-");
		knownBuiltInTokens.put("EQUALS", "=");
		knownBuiltInTokens.put("QMARK", "?");
		knownBuiltInTokens.put("BANG", "!");
		knownBuiltInTokens.put("DOLLAR", "$");
		knownBuiltInTokens.put("HASH", "#");
		knownBuiltInTokens.put("AT", "@");
		knownBuiltInTokens.put("DOT_DOT", "..");
		knownBuiltInTokens.put("MINUS_GT", "->");
		knownBuiltInTokens.put("GT_LT", "><");
		knownBuiltInTokens.put("LT_GT", "<>");
		knownBuiltInTokens.put("AMP", "&");
		knownBuiltInTokens.put("LT", "<");
		knownBuiltInTokens.put("GT", ">");
		knownBuiltInTokens.put("PIPE", "|");
		knownBuiltInTokens.put("SLASH", "/");
	}

	public String languageName;

	public Map<String, String> options = new HashMap<String, String>();

	public final List<TokenRuleCS> tokenRules = new ArrayList<TokenRuleCS>();

	public final List<AltRuleCS> altRules = new ArrayList<AltRuleCS>();

	public final List<SeqRuleCS> seqRules = new ArrayList<SeqRuleCS>();

	public final List<ListRuleCS> listRules = new ArrayList<ListRuleCS>();

	private List<RuleCS> _lazyAllRules = null;

	public List<RuleCS> allRules() {
		if (_lazyAllRules == null) {
			_lazyAllRules = new ArrayList<RuleCS>();
			_lazyAllRules.addAll(tokenRules);
			_lazyAllRules.addAll(altRules);
			_lazyAllRules.addAll(seqRules);
			_lazyAllRules.addAll(listRules);
		}
		return _lazyAllRules;
	}

	/**
	 * There should be exactly one entry rule.
	 */
	public List<RuleCS> entryRules() {
		List<RuleCS> res = new ArrayList<RuleCS>();
		for (RuleCS ruleCS : allRules()) {
			if (ruleCS.attrs.contains("entry")) {
				res.add(ruleCS);
			}
		}
		return res;
	}

	public List<RuleCS> rulesWithDuplicateNames() {
		Set<String> seenRuleNames = new HashSet<String>();
		Set<String> dupNames = new HashSet<String>();
		for (RuleCS ruleCS : allRules()) {
			if (seenRuleNames.contains(ruleCS.name)) {
				dupNames.add(ruleCS.name);
			}
			seenRuleNames.add(ruleCS.name);
		}
		List<RuleCS> res = new ArrayList<RuleCS>();
		for (RuleCS ruleCS : allRules()) {
			if (dupNames.contains(ruleCS.name)) {
				res.add(ruleCS);
			}
		}
		return res;
	}

	/**
	 * A token rule should consist only of primitive alternatives: fixed
	 * keywords, other token rules, or built-in tokens (ID, STRING_LITERAL,
	 * CHAR_LITERAL, INT, collectively "canBeConsideredToken"). Additionally,
	 * The name of a TokenRule may not shadow that of a built-in token.
	 */
	public List<TokenRuleCS> malFormedTokenRules() {
		List<TokenRuleCS> res = new ArrayList<TokenRuleCS>();
		for (TokenRuleCS trCS : tokenRules) {
			if (trCS.getKindOfAlts() == TokenRuleAltsKind.MALFORMED) {
				res.add(trCS);
			} else if (isBuiltInToken(trCS.name)) {
				/*
				 * The name of a TokenRule may not shadow that of a built-in
				 * token.
				 */
				res.add(trCS);
			} else if (trCS.isEmpty()) {
				res.add(trCS);
			}
		}
		return res;
	}

	/**
	 * Reject seq rules having: <br>
	 * (a) a consituent whose name can't be resolved (if resolved, its
	 * well-formedness is checked together with all rules of its kind, so that's
	 * not checked again in this method), <br>
	 * (b) duplicate fields, among the specified field names (they are optional)
	 * <br>
	 * 
	 * A SeqRule may contain optional lists, alternatives, sequences, and
	 * tokens.
	 * 
	 */
	public Set<SeqRuleCS> malFormedSeqRules() {
		Set<SeqRuleCS> res = new HashSet<SeqRuleCS>();
		for (SeqRuleCS sCS : seqRules) {
			if (sCS.isMalformed(this)) {
				res.add(sCS);
			}
		}
		return res;
	}

	/**
	 * Reject alt rules having: <br>
	 * (a) an alternative whose name can't be resolved (if resolved, its
	 * well-formedness is checked together with all rules of its kind, so that's
	 * not checked again in this method), <br>
	 * (b) one or more constituent alternative of list kind <br>
	 * (c) an alt rule, when its constituents are recursively exploded, should
	 * contain two or more alternatives.<br>
	 * (d) mixed tokens and rules (seq, alt) among the alternatives.<br>
	 * 
	 */
	public Set<AltRuleCS> malFormedAltRules() {
		Set<AltRuleCS> res = new HashSet<AltRuleCS>();
		for (AltRuleCS aCS : altRules) {
			if (aCS.isMalformed()
					|| aCS.getKindOfAlts() == AltRuleAltsKind.MALFORMED) {
				res.add(aCS);
			}
		}
		return res;
	}

	SeqRuleCS findSeqRuleCSByName(String name) {
		for (SeqRuleCS srCS : seqRules) {
			if (namesMatch(name, srCS.name)) {
				return srCS;
			}
		}
		return null;
	}

	AltRuleCS findAltRuleCSByName(String innerAltName) {
		for (AltRuleCS aCS : altRules) {
			if (namesMatch(innerAltName, aCS.name)) {
				return aCS;
			}
		}
		return null;
	}

	/**
	 * Reject list rules: <br>
	 * (a) with a non-well-formed repeating item, <br>
	 * (b) with a 2nd repeating item (if specified) different from the first
	 * (thus, we assume repetition consists of only one kind of repeating item)
	 * <br>
	 * (c) containing a list as repeating item <br>
	 * 
	 * A list containing a non-constant separator is not rejected (e.g.,
	 * Emfatic.ast contains the token token qidSeparator : DOT | DOLLAR which is
	 * used as separator. However, the converter to EMF will not store
	 * separators.
	 * 
	 */
	public Set<ListRuleCS> malFormedListRules() {
		Set<ListRuleCS> res = new HashSet<ListRuleCS>();
		for (ListRuleCS lrCS : listRules) {
			if (lrCS.isMalformed(this)) {
				res.add(lrCS);
			}
		}
		return res;
	}

	public ListRuleCS findListRuleCSByName(String name) {
		for (ListRuleCS lrCS : listRules) {
			if (namesMatch(name, lrCS.name)) {
				return lrCS;
			}
		}
		return null;
	}

	boolean isRuleName(String name) {
		for (RuleCS rCS : allRules()) {
			if (namesMatch(name, rCS.name)) {
				return true;
			}
		}
		return false;
	}

	public boolean canBeConsideredToken(String name) {
		if (isBuiltInToken(name) || name.equals("STRING_LITERAL")
				|| name.equals("INT_LITERAL") || name.equals("CHAR_LITERAL")
				|| name.equals("ID") || isSurroundedByQuotes(name)) {
			return true;
		}
		return false;
	}

	public boolean isBuiltInToken(String alt) {
		boolean res = knownBuiltInTokens.keySet().contains(alt);
		return res;
	}

	public TokenRuleCS findTokenRuleCSByName(String lookup) {
		for (TokenRuleCS trCS : tokenRules) {
			if (namesMatch(trCS.name, lookup)) {
				return trCS;
			}
		}
		return null;
	}

	public boolean namesMatch(String name, String lookup) {
		boolean res = name.compareToIgnoreCase(lookup) == 0;
		return res;
	}

	boolean isIntegerLiteral(String alt) {
		if (isSurroundedByQuotes(alt)) {
			alt = alt.substring(1, alt.length() - 1);
		}
		try {
			Integer.valueOf(alt);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isSurroundedByQuotes(String alt) {
		boolean res = alt.startsWith("\"") && alt.endsWith("\"");
		return res;
	}

	public boolean isWellFormed() {
		boolean res = entryRules().size() == 1;
		res &= malFormedTokenRules().isEmpty();
		res &= rulesWithDuplicateNames().isEmpty();
		res &= malFormedListRules().isEmpty();
		res &= malFormedAltRules().isEmpty();
		res &= malFormedSeqRules().isEmpty();
		res &= missingGrammarOptions().isEmpty();
		res &= namingConventionsAreFollowed();
		return res;
	}

	private boolean namingConventionsAreFollowed() {
		boolean res = namingConventionLanguageNameStartsLowercase();
		return res;
	}

	private boolean namingConventionLanguageNameStartsLowercase() {
		boolean res = Character.isLowerCase(languageName.charAt(0));
		return res;
	}

	public static RootCS getWellFormednessChecker(IFile astFile) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(astFile
					.getContents()));
			GymnastWalker<Object> gw = new GymnastWalker<Object>();
			RootCS wellFormednessChecker = new RootCS();
			GymnastCollector vCollect = new GymnastCollector(
					wellFormednessChecker);
			gw.walk(reader, vCollect);
			boolean res = wellFormednessChecker.isWellFormed();
			if (res) {
				return wellFormednessChecker;
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void addErrorMsgsFromInputValidation(ParseContext parseContext) {
		assert parseContext != null;
		if (entryRules().size() == 0) {
			parseContext.addParseMessage(new GeneralMessage(
					"No seq rule has been declared as [entry]"));
		}
		if (entryRules().size() > 1) {
			for (RuleCS ruleCS : entryRules()) {
				MultipleEntryRules parseMessage = new MultipleEntryRules(ruleCS);
				parseContext.addParseMessage(parseMessage);
			}
		}
		for (TokenRuleCS trCS : malFormedTokenRules()) {
			trCS.addParseMessages(parseContext);
		}
		for (RuleCS rCS : rulesWithDuplicateNames()) {
			RuleWithNameDuplicate parseMessage = new RuleWithNameDuplicate(rCS);
			parseContext.addParseMessage(parseMessage);
		}
		for (ListRuleCS lrCS : malFormedListRules()) {
			lrCS.addParseMessages(parseContext);
		}
		for (AltRuleCS arCS : malFormedAltRules()) {
			arCS.addParseMessages(parseContext);
		}
		for (SeqRuleCS srCS : malFormedSeqRules()) {
			srCS.addParseMessages(parseContext);
		}
		for (String str : missingGrammarOptions()) {
			MissingGrammarOption parseMessage = new MissingGrammarOption(str);
			parseContext.addParseMessage(parseMessage);
		}
		if (!namingConventionLanguageNameStartsLowercase()) {
			GeneralMessage parseMessage = new GeneralMessage(
					"The language name ("
							+ languageName
							+ ") should start with a lowercase given that it'll be used later as package name.");
			parseContext.addParseMessage(parseMessage);
		}
		addWarningToDocumentDefaultOptions(parseContext);
	}

	private boolean addWarningToDocumentDefaultOptions(ParseContext parseContext) {
		boolean oneOrMoreMissing = false;
		for (String opt : allOptionsSupported()) {
			if (!options.containsKey(opt)) {
				/*
				 * in case no umbrella type is to be generated then no name for
				 * it need be specified
				 */
				boolean skip = opt.toLowerCase().equals("umbrellatypename")
						&& !getOption_addUmbrellaType();
				if (!skip) {
					oneOrMoreMissing = true;
					String msg = "An option supported by Grammar2Ecore is missing ("
							+ opt + "). ";
					msg += "Although a default values has been provided, including it explicitly in the .ast serves as documentation. ";
					ParseWarning parseMessage = new Grammar2EcoreParseWarning.GeneralWarning(
							msg, 0, 1);
					parseContext.addParseMessage(parseMessage);
				}
			}
		}
		return oneOrMoreMissing;
	}

	public Set<String> allOptionsSupported() {
		String[] allOptionsSupported = new String[] { "prettyPrinter",
				"unparser", "ecorizer", "umbrellaType", "umbrellaTypeName" };
		HashSet<String> res = new HashSet<String>(Arrays
				.asList(allOptionsSupported));
		return res;
	}

	public List<String> missingGrammarOptions() {
		List<String> res = new ArrayList<String>();
		if (!options.containsKey("astPackageName")) {
			/*
			 * This is the Java package for the Gymnast-generated classes for
			 * productions. It is necessary when generating the Ecorizer class,
			 * to convert instances of those classes into instances of their
			 * Ecore-based couneterparts.
			 */
			res.add("astPackageName");
		}

		if (!options.containsKey("parserPackageName")) {
			res.add("parserPackageName");
		}

		/*
		 * genModelBasePackage should also be specified (for the Java classes
		 * generated from Ecore). It is necessary when generating the Ecorizer
		 * class, as part of the return type.
		 */
		if (!options.containsKey("genModelBasePackage")) {
			/*
			 * This is the Java package for the Gymnast-generated classes for
			 * productions. It is necessary when generating the Ecorizer class,
			 * to convert instances of those classes into instances of their
			 * Ecore-based couneterparts.
			 */
			res.add("genModelBasePackage");
		}

		if (!options.containsKey("genModelPrefix")) {
			res.add("genModelPrefix");
		}

		/*
		 * astBaseClassName is needed to make the case distinction in the
		 * generated core of Ecorizer. If not specified, a default is generated,
		 * see getOption_astBaseClassName().
		 */
		return res;
	}

	public boolean canBeResolved(String name) {
		if (canBeConsideredToken(name)) {
			return true;
		}
		if (isRuleName(name)) {
			return true;
		}
		return false;
	}

	public Set<String> getNamesOfAltRules() {
		Set<String> res = new HashSet<String>();
		for (AltRuleCS arCS : altRules) {
			res.add(arCS.name);
		}
		return res;
	}

	public Set<String> getNamesOfTokenRules() {
		Set<String> res = new HashSet<String>();
		for (TokenRuleCS trCS : tokenRules) {
			res.add(trCS.name);
		}
		return res;
	}

	public Set<String> getNamesOfListRules() {
		Set<String> res = new HashSet<String>();
		for (ListRuleCS lrCS : listRules) {
			res.add(lrCS.name);
		}
		return res;
	}

	/**
	 * For example, for the input "DOT" returns "."
	 * 
	 * @param tokenName
	 * @return
	 */
	public static String getValueOfBuiltInToken(String tokenName) {
		String res = knownBuiltInTokens.get(tokenName);
		if (res == null) {
			return "";
		} else {
			return res;
		}
	}

	public String getOption_astPackageName() {
		return helperGetOption("astPackageName");
	}

	private String helperGetOption(String option) {
		String res = "";
		String userChoice = options.get(option);
		if (userChoice != null && !userChoice.equals("")) {
			res = unquote(userChoice);
		}
		return res;
	}

	public List<String> unquote(List<String> enumLits) {
		List<String> res = new ArrayList<String>();
		for (String s : enumLits) {
			if (s.startsWith("\"") && s.endsWith("\"")) {
				s = s.substring(1, s.length() - 1);
			}
			res.add(s);
		}
		return res;
	}

	public static String unquote(String s) {
		String res = s.substring(1, s.length() - 1);
		return res;
	}

	/**
	 * Uppercase the first letter of the name to make a good Java class name (so
	 * "myName" -> "MyName")
	 * 
	 * @param name
	 * @return
	 */
	public static String camelCase(String name) {
		String res = org.eclipse.gymnast.generator.core.generator.Util
				.toUppercaseName(name);
		return res;
	}

	public String getOption_genModelBasePackage() {
		String res = helperGetOption("genModelBasePackage");
		return res;
	}

	public String getOption_genModelPrefix() {
		String res = helperGetOption("genModelPrefix");
		if (res.equals("")) {
			res = "";
		}
		return res;
	}

	public String getOption_astBaseClassName() {
		String res = helperGetOption("astBaseClassName");
		if (res.equals("")) {
			res = camelCase(languageName) + "ASTNode";
		}
		return res;
	}

	public boolean getOption_unparser() {
		String str = helperGetOption("unparser");
		boolean res = isOneOf(str, "no", "false");
		return !res;
	}

	public boolean getOption_prettyPrinter() {
		String str = helperGetOption("prettyPrinter");
		boolean res = isOneOf(str, "no", "false");
		return !res;
	}

	public boolean getOption_ecorizer() {
		String str = helperGetOption("ecorizer");
		boolean res = isOneOf(str, "no", "false");
		return !res;
	}

	public boolean getOption_addUmbrellaType() {
		String str = helperGetOption("umbrellaType");
		boolean res = isOneOf(str, "no", "false");
		return !res;
	}

	public String getOption_umbrellaTypeName() {
		String str = helperGetOption("umbrellaTypeName");
		return str;
	}

	/**
	 * reports whether the first argument is among those listed next. Useful
	 * when comparing user-provided text with alternative spellings for it
	 * (e.g., "true" and "yes").
	 */
	private boolean isOneOf(String lookUp, String... alternatives) {
		if (lookUp == null || lookUp.equals("")) {
			return false;
		}
		lookUp = lookUp.trim().toLowerCase();
		for (String alt : alternatives) {
			alt = alt.trim().toLowerCase();
			if (lookUp.equals(alt)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Precondition:
	 * 
	 * Grammar2Ecore.convert() has run
	 * 
	 * @see TokenRuleCS.enu
	 * 
	 */
	public List<TokenRuleCS> getTokenRulesWithEnums() {
		List<TokenRuleCS> res = new ArrayList<TokenRuleCS>();
		for (TokenRuleCS trCS : tokenRules) {
			if (trCS.enu != null) {
				res.add(trCS);
			}
		}
		return res;
	}

	public List<AltRuleCS> getAltRulesWithEnums() {
		List<AltRuleCS> res = new ArrayList<AltRuleCS>();
		for (AltRuleCS arCS : altRules) {
			if (arCS.enu != null) {
				res.add(arCS);
			}
		}
		return res;
	}

	public List<AltRuleCS> getAltRulesWithEClasses() {
		List<AltRuleCS> res = new ArrayList<AltRuleCS>();
		for (AltRuleCS arCS : altRules) {
			if (arCS.eClass != null) {
				res.add(arCS);
			}
		}
		return res;
	}

	/**
	 * The name of the EClassifier created by MyEcoreUtil may differ from that
	 * given in the .ast file.
	 * 
	 * @param name
	 * @return
	 */
	public EClassifier getETypeForRuleName(String name) {
		EClassifier res = getETypeForSeqRuleName(name);
		if (res != null) {
			return res;
		}
		res = getETypeForAltRuleName(name);
		if (res != null) {
			return res;
		}
		res = getETypeForTokenRuleName(name);
		return res;
	}

	private EClassifier getETypeForTokenRuleName(String name) {
		for (TokenRuleCS trCS : getTokenRulesWithEnums()) {
			if (namesMatch(trCS.name, name)) {
				return trCS.enu;
			}
		}
		RuleCS r = getRuleForNameIfAny(name);
		if (r != null && r.canBeRegardedAsBoolean()) {
			return EcorePackage.eINSTANCE.getEBoolean();
		}
		return null;
	}

	private EClassifier getETypeForAltRuleName(String name) {
		for (AltRuleCS anAltRule : getAltRulesWithEClasses()) {
			if (namesMatch(anAltRule.name, name)) {
				return anAltRule.eClass;
			}
		}
		for (AltRuleCS anotherAlt : getAltRulesWithEnums()) {
			if (namesMatch(anotherAlt.name, name)) {
				return anotherAlt.enu;
			}
		}
		RuleCS r = getRuleForNameIfAny(name);
		if (r != null && r.canBeRegardedAsBoolean()) {
			return EcorePackage.eINSTANCE.getEBoolean();
		}
		return null;
	}

	public EClass getETypeForSeqRuleName(String name) {
		for (SeqRuleCS srCS : seqRules) {
			if (namesMatch(srCS.name, name)) {
				return srCS.eClass;
			}
		}
		return null;
	}

	public List<String> getAltRulesWithIntsOnly() {
		ArrayList<String> res = new ArrayList<String>();
		for (AltRuleCS arCS : altRules) {
			if (arCS.getKindOfAlts() == AltRuleAltsKind.INTS) {
				res.add(arCS.name);
			}
		}
		return res;
	}

	public List<String> getTokenRulesWithIntsOnly() {
		ArrayList<String> res = new ArrayList<String>();
		for (TokenRuleCS trCS : tokenRules) {
			if (trCS.getKindOfAlts() == TokenRuleAltsKind.INTS) {
				res.add(trCS.name);
			}
		}
		return res;
	}

	public List<String> getAltOrTokenRulesWithIntsOnly() {
		ArrayList<String> res = new ArrayList<String>();
		res.addAll(getAltRulesWithIntsOnly());
		res.addAll(getTokenRulesWithIntsOnly());
		return res;
	}

	public List<String> getAltRulesWithSeqOnly() {
		ArrayList<String> res = new ArrayList<String>();
		for (AltRuleCS arCS : altRules) {
			if (arCS.getKindOfAlts() == AltRuleAltsKind.CONTAINSSEQ) {
				res.add(arCS.name);
			}
		}
		return res;
	}

	public List<String> getAltRulesWithStrings() {
		ArrayList<String> res = new ArrayList<String>();
		for (AltRuleCS arCS : altRules) {
			if (arCS.getKindOfAlts() == AltRuleAltsKind.ID_OR_MIXEDSTRINT) {
				res.add(arCS.name);
			}
		}
		return res;
	}

	public List<String> getTokenRulesWithStrings() {
		ArrayList<String> res = new ArrayList<String>();
		for (TokenRuleCS trCS : tokenRules) {
			if (trCS.getKindOfAlts() == TokenRuleAltsKind.ID_OR_MIXEDSTRINT) {
				res.add(trCS.name);
			}
		}
		return res;
	}

	public List<String> getAltOrTokenRulesWithStrings() {
		ArrayList<String> res = new ArrayList<String>();
		res.addAll(getAltRulesWithStrings());
		res.addAll(getTokenRulesWithStrings());
		return res;
	}

	/**
	 * For example, camelCaseIBM -> CAMEL_CASE_IBM
	 * 
	 * @param name
	 * @return
	 */
	public String emfUpperCase(String name) {
		// FIXME
		return name.toUpperCase();
	}

	public RuleCS getRuleForNameIfAny(String lookup) {
		for (RuleCS r : allRules()) {
			if (namesMatch(r.name, lookup)) {
				return r;
			}
		}
		return null;
	}

	public String languageFactoryImpl(SeqRuleCS srCS) {
		// For example, EmfaticFactoryImpl
		String res = getOption_genModelBasePackage() + ".";
		res += srCS.eClass.getEPackage().getName() + ".impl.";
		res += getOption_genModelPrefix();
		res += "FactoryImpl";
		return res;
	}

	public String getOption_parserPackageName() {
		return helperGetOption("parserPackageName");
	}

	public static final String ATTRIBUTE_REGARD_AS_BOOLEAN = "boolean";

}
