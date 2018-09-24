pipeline {
  agent any
  triggers {
    upstream '/metaborg/spoofax-releng/master'
  }
  options {
    buildDiscarder logRotator(numToKeepStr: '3')
    disableConcurrentBuilds()
    timeout(time: 1, unit: 'HOURS')
  }
  environment {
    mavenRepo = '.m2repo'
    mavenOpts = '-Xmx1G -Xss16M'
    mavenSettings = 'metaborg-mirror-maven-global-config'
    mavenDeploySettings = 'metaborg-mirror-deploy-global-maven-config'
    dateTime = sh(returnStdout: true, script: 'date +%Y%m%d%H%M')
  }
  stages {
    stage('Clean') {
      steps {
        sh 'git clean -ddffxx'
      }
    }
    stage('Build & Test') {
      steps {
      withMaven(mavenLocalRepo: mavenRepo, mavenOpts: mavenOpts, globalMavenSettingsConfig: mavenSettings) {
          sh "mvn -B -U clean verify -DforceContextQualifier=$dateTime"
        }
      }
    }
    stage('Deploy') {
      when {
        not { changeRequest() }
        anyOf {
          branch 'master'
          branch 'release'
          tag 'v*'
        }
      }
      steps {
        withMaven(mavenLocalRepo: mavenRepo, mavenOpts: mavenOpts, globalMavenSettingsConfig: mavenDeploySettings) {
          sh "mvn -B -U deploy -DforceContextQualifier=$dateTime -DskipTests -Dmaven.test.skip=true"
        }
      }
    }
  }
  post {
    success {
      archiveArtifacts(artifacts: 'lang.java.eclipse.site/target/site/', excludes: null)
    }
    failure {
      slackSend(color: 'danger', channel: '#spoofax-dev', message: "${env.JOB_NAME} - ${env.BUILD_NUMBER} - failed :facepalm: (<${env.BUILD_URL}|Status> <${env.BUILD_URL}console|Console>)")
    }
    fixed {
      slackSend(color: 'good', channel: '#spoofax-dev', message: "${env.JOB_NAME} - ${env.BUILD_NUMBER} - fixed :party_parrot: (<${env.BUILD_URL}|Status> <${env.BUILD_URL}console|Console>)")
    }
    cleanup {
      sh 'git clean -ddffxx'
    }
  }
}
