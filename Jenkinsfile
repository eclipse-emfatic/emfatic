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
              sh 'mvn -B -T 1C clean install -P eclipse-sign'
            }
          }
          stage('Plain Maven build') {
            steps {
              sh 'mvn -B -T 1C -f pom-plain.xml compile'
            }
          }
        }
      }
    }
}
