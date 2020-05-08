pipeline {
  agent any
  tools {
        maven 'Maven3'
        jdk 'Java8'
  }
  options {
    buildDiscarder logRotator(numToKeepStr: '10')
  }
  stages {
    stage('Clean') {
      steps {
        sh 'mvn clean'
      }
    }
    stage('Compile') {
      steps {
        sh 'mvn compile'
      }
    }
    stage('Test') {
      steps {
        sh 'mvn test'
        junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
      }
    }
    stage('Package') {
      steps {
        sh 'mvn package'
      }
    }
    stage('Re-package') {
      steps {
        sh 'mvn package javadoc:aggregate-jar'
      }
    }
    stage('Release ZIP') {
      steps {
        sh '''mkdir -p temp;
        cp SimpleNameTagPlugin/target/*.jar temp/;'''
        zip archive: true, dir: 'temp', glob: '', zipFile: 'SimpleNameTag.zip'
        sh 'rm -r temp/';
      }
    }
    stage('Archive') {
      steps {
        archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/SimpleNameTag*.jar', fingerprint: true, onlyIfSuccessful: true
      }
    }
  }
  post {
    always {
      withCredentials([string(credentialsId: 'cloudnet-discord-ci-webhook', variable: 'url')]) {
        discordSend description: 'New build for SignSystem!', footer: 'New build!', link: env.BUILD_URL, successful: currentBuild.resultIsBetterOrEqualTo('SUCCESS'), title: JOB_NAME, webhookURL: url
      }
    }
  }
}