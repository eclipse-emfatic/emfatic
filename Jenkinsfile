pipeline {
    agent {
      kubernetes {
        label 'migration'
      }
    }
    options {
      disableConcurrentBuilds()
      buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '2', daysToKeepStr: '14', numToKeepStr: ''))
    }
    tools {
        maven 'apache-maven-latest'
        jdk 'openjdk-jdk11-latest'
    }
    triggers {
        pollSCM('H/5 * * * *')
    }
    stages {
      stage('Main') {
        stages {
          stage('Build') {
            steps {
              sh 'mvn -B -T 1C clean verify -P eclipse-sign'
            }
          }
          stage('Plain Maven build') {
            steps {
              sh 'mvn -B -T 1C -f pom-plain.xml compile'
            }
          }
          stage('Update site') {
            when {
              anyOf {
                branch 'master'
                branch 'agarciad/ossrh'
              }
            }
            steps {
              sh 'mvn -B package -DskipTests'
              lock('download-area') {
                sshagent (['projects-storage.eclipse.org-bot-ssh']) {
                  sh '''
                    INTERIM=/home/data/httpd/download.eclipse.org/emfatic/interim-jenkins
                    SITEDIR="$WORKSPACE/releng/org.eclipse.emf.emfatic.updatesite/target"
                    if [ -d "$SITEDIR" ]; then
                      ssh genie.emfatic@projects-storage.eclipse.org rm -rf $INTERIM
                      scp -r "$SITEDIR/repository" genie.emfatic@projects-storage.eclipse.org:$INTERIM
                      scp "$SITEDIR"/*.zip genie.emfatic@projects-storage.eclipse.org:$INTERIM/emfatic-interim-site.zip
                    fi
                  '''
                }
              }
            }
          }
          stage('Deploy to OSSRH') {
            when {
              anyOf {
                branch 'master'
                branch 'agarciad/ossrh'
              }
            }
            environment {
              KEYRING = credentials('secret-subkeys.asc')
            }
            steps {
              sh '''
                gpg --batch --import "${KEYRING}"
                for fpr in $(gpg --list-keys --with-colons  | awk -F: '/fpr:/ {print $10}' | sort -u);
                do
                  echo -e "5\ny\n" |  gpg --batch --command-fd 0 --expert --edit-key $fpr trust;
                done
              '''
              lock('ossrh') {
                sh 'mvn -B -f pom-plain.xml -P ossrh,eclipse-sign deploy'
              }
            }
          }
        }
      }
    }
}
