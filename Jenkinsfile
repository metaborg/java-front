pipeline {
  agent any
  triggers {
    snapshotDependencies()
  }
  options {
    buildDiscarder logRotator(numToKeepStr: '3')
    disableConcurrentBuilds()
    durabilityHint 'PERFORMANCE_OPTIMIZED'
    timeout(time: 1, unit: 'HOURS')
  }
  environment {
    mavenRepo = '.m2repo'
    mavenOpts = '-Xmx1G -Xss16M'
    mavenBuildSettings = 'metaborg-mirror-maven-global-config'
    mavenDeploySettings = 'metaborg-mirror-deploy-global-maven-config'
    dateTime = sh(returnStdout: true, script: 'date +%Y%m%d%H%M').trim()
  }
  stages {
    stage('Clean') {
      steps {
        sh 'git clean -ddffxx'
      }
    }
    stage('Build & Test') {
      steps {
        withMaven(mavenLocalRepo: mavenRepo, mavenOpts: mavenOpts, globalMavenSettingsConfig: mavenBuildSettings) {
          sh "mvn -B -U clean verify -DforceContextQualifier=$dateTime"
        }
      }
    }
    stage('Deploy Release') {
      when {
        tag 'v*'
      } // Deploy release artifacts only on release branch, when a commit is tagged with a v* (e.g., v0.1.0) tag.
      steps {
        withMaven(mavenLocalRepo: mavenRepo, mavenOpts: mavenOpts, globalMavenSettingsConfig: mavenDeploySettings) {
          sh "mvn -B -nsu deploy -DskipTests -Dmaven.test.skip=true -P release"
        } // Add release profile (-P release) to enforce that no snapshot dependencies are used in a release.
      }
    }
    stage('Deploy Snapshot') {
      when {
        branch 'master'
      } // Deploy snapshot artifacts only on master branch.
      steps {
        withMaven(mavenLocalRepo: mavenRepo, mavenOpts: mavenOpts, globalMavenSettingsConfig: mavenDeploySettings) {
          sh "mvn -B -nsu deploy -DskipTests -Dmaven.test.skip=true -DforceContextQualifier=$dateTime"
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
