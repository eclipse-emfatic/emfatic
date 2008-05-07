package org.eclipse.gymnast.generators.ecore.convert;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.gymnast.generators.ecore.cst.GymnastCollector;
import org.eclipse.gymnast.generators.ecore.cst.RootCS;
import org.eclipse.gymnast.generators.ecore.errors.Grammar2EcoreInvalidInput;
import org.eclipse.gymnast.generators.ecore.errors.Grammar2EcoreInvalidInput.EmptyTokenRule.CoverageOfAltAndTokenRules;
import org.eclipse.gymnast.generators.ecore.walker.GymnastWalker;
import org.eclipse.gymnast.runtime.core.parser.ParseContext;
import org.eclipse.gymnast.runtime.core.parser.ParseMessage;
import org.eclipse.gymnast.runtime.core.util.MarkerUtil;

public class EcoreGeneratorFromGymnastGrammar {

	public void generate(IFile emfFile, IProgressMonitor monitor) {
		generate(emfFile, true, monitor);
	}

	public boolean generate(java.io.File astFile, java.io.File ecoreFile) {

		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(astFile)));
			GymnastWalker<Object> gw = new GymnastWalker<Object>();
			RootCS wellFormednessChecker = new RootCS();
			GymnastCollector vCollect = new GymnastCollector(wellFormednessChecker);
			gw.walk(reader, vCollect);

			Grammar2Ecore g2e = new Grammar2Ecore(wellFormednessChecker);

			boolean bCheckAltToken = checkCoverageOfAltAndTokenRules(wellFormednessChecker, g2e);
			if (!bCheckAltToken || !wellFormednessChecker.isWellFormed()) {
				return false;
			}
			g2e.convert();
			EPackage rootPackage = g2e.eP;
			try {
				MyEcoreUtil.serializeEcoreToFile(ecoreFile.getAbsolutePath(), rootPackage);
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;
	}

	public void generate(IFile astFile, boolean writeEcore, IProgressMonitor monitor) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(astFile.getContents()));
			GymnastWalker<Object> gw = new GymnastWalker<Object>();
			RootCS wellFormednessChecker = new RootCS();
			GymnastCollector vCollect = new GymnastCollector(wellFormednessChecker);
			gw.walk(reader, vCollect);

			Grammar2Ecore g2e = new Grammar2Ecore(wellFormednessChecker);

			ParseContext parseContext = new ParseContext();

			boolean bCheckAltToken = checkCoverageOfAltAndTokenRules(wellFormednessChecker, g2e);
			if (!bCheckAltToken) {
				parseContext.addParseMessage(new CoverageOfAltAndTokenRules());
			}

			wellFormednessChecker.addErrorMsgsFromInputValidation(parseContext);
			MarkerUtil.updateMarkers(astFile, parseContext);
			if (!wellFormednessChecker.isWellFormed()) {
				return;
			}

			g2e.convert();

			boolean showStopper = false;
			/*
			 * All markers generated above are showstopper. If some of them
			 * where only warnings, the check below could have been used (and
			 * still generate an .ecore that can be opened with an editor to
			 * correct such warnings).
			 */
			for (ParseMessage pm : parseContext.getMessages()) {
				showStopper |= (pm instanceof Grammar2EcoreInvalidInput);
			}

			if (!showStopper && writeEcore) {
				String filePath = getEcoreFilePath(astFile);
				EPackage rootPackage = g2e.eP;
				MyEcoreUtil.serializeEcoreToFile(filePath, rootPackage);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String getEcoreFilePath(IFile astFile) {
		String astFileExt = astFile.getFileExtension();
		int extLen = astFileExt != null ? astFileExt.length() + 1 : 0;
		String astFileName = astFile.getName();
		String fileName = astFileName.substring(0, astFileName.length() - extLen);
		fileName = fileName + ".ecore";
		String filePath = astFile.getFullPath().removeLastSegments(1).append(fileName).toString();
		return filePath;
	}

	/**
	 * By now the decision has been made which TokenRules and AltRules will be
	 * converted to: (a) Ecore-level enumerations, (b) String, (c) Integer.
	 * Additionally some AltRules will be converted to (d) Ecore-level
	 * interfaces. Any given TokenRule or AltRule must be mapped to only one of
	 * (a)-(d).
	 * 
	 * @param wellFormednessChecker
	 * @param g2e
	 */
	private boolean checkCoverageOfAltAndTokenRules(RootCS wellFormednessChecker, Grammar2Ecore g2e) {
		List<String> tokensAsEnu = g2e.getTokenRulesWithFixedKeywords();

		// no AltRule or TokenRule is mapped to more than one category
		List<String> altsAsEnu = g2e.getAltRulesWithFixedKeywords();

		List<String> altsAsString = wellFormednessChecker.getAltRulesWithStrings();
		List<String> tokensAsString = wellFormednessChecker.getTokenRulesWithStrings();
		List<String> altsAsInt = wellFormednessChecker.getAltRulesWithIntsOnly();
		List<String> tokensAsInt = wellFormednessChecker.getTokenRulesWithIntsOnly();
		List<String> altsAsSeq = wellFormednessChecker.getAltRulesWithSeqOnly();

		boolean res = allDisjoint(tokensAsEnu, altsAsEnu, altsAsString, tokensAsString, altsAsInt, tokensAsInt,
				altsAsSeq);

		// all AltRules are mapped
		Set<String> altsAsSthg = new HashSet<String>();
		altsAsSthg.addAll(altsAsEnu);
		altsAsSthg.addAll(altsAsInt);
		altsAsSthg.addAll(altsAsString);
		altsAsSthg.addAll(altsAsSeq);
		res &= wellFormednessChecker.getNamesOfAltRules().equals(altsAsSthg);

		// all TokenRules are mapped
		Set<String> tokensAsSthg = new HashSet<String>();
		tokensAsSthg.addAll(tokensAsEnu);
		tokensAsSthg.addAll(tokensAsInt);
		tokensAsSthg.addAll(tokensAsString);
		Set<String> allTokenRulesNames = wellFormednessChecker.getNamesOfTokenRules();
		res &= allTokenRulesNames.equals(tokensAsSthg);

		return res;
	}

	private boolean allDisjoint(List<String>... lists) {
		Set<String> seenSoFar = new HashSet<String>();
		for (List<String> list : lists) {
			for (String string : list) {
				if (seenSoFar.contains(string)) {
					return false;
				}
			}
		}
		return true;
	}

}
