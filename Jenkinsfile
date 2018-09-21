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
  stages {
    stage('Clean') {
      steps {
        sh 'git clean -ddffxx'
      }
    }
    stage('Build/Test/Deploy') {
      steps {
        withMaven(mavenLocalRepo: ".repository", mavenOpts: '-Xmx1G -Xms1G -Xss16m') {
          sh 'mvn -B -U clean verify -DforceContextQualifier=\$(date +%Y%m%d%H%M)'
        }
      }
    }
  }
  post {
    success {
      archiveArtifacts(artifacts: 'lang.java.eclipse.site/target/site/', excludes: null)
    }
    failure {
      slackSend(color: 'danger', channel: '#spoofax-dev', message: "Failed: ${env.JOB_NAME} [${env.BUILD_NUMBER}] (<${env.BUILD_URL}|Status> <${env.BUILD_URL}console|Console>)")
    }
    fixed {
      slackSend(color: 'good', channel: '#spoofax-dev', message: "Fixed: ${env.JOB_NAME} [${env.BUILD_NUMBER}] (<${env.BUILD_URL}|Status> <${env.BUILD_URL}console|Console>)")
    }
    cleanup {
      sh 'git clean -ddffxx'
    }
  }
}
