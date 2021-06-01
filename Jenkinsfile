#!groovy
@Library('metaborg.jenkins.pipeline') _

spoofaxCoreLanguagePipeline(
  upstreamProjects: ['/metaborg/spoofax-releng/master'],
  mavenGlobalSettingsFilePath: '.mvn/settings.xml',
  mavenOpts: '-Xmx2G -Xss16M',
  slack: true
)
