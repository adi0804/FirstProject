#!/usr/bin/env groovy
pipeline {
	agent any
	 environment {
    //Use Pipeline Utility Steps plugin to read information from pom.xml into env variables
    IMAGE = readMavenPom().getArtifactId()
    VERSION = readMavenPom().getVersion()
    }
//	parameters {
//  choice choices: 'master'\n'develop', description: 'Multiple Branches', name: 'branch'
//}
	options {
      		buildDiscarder(logRotator(numToKeepStr: '3'))
    		}
	stages{
		stage('Build'){
			steps{
				echo "Build Stage"
				//sh 'printenv'0
    				//if (env.BRANCH_NAME != null) {
					echo "${env.BRANCH_NAME}"
   				//}
			}
			}
		stage('Test'){
			steps{
				echo "Test Stage"
				echo "${VERSION}"
				echo "${env.PIPELINE_NAME}"
			}
		}
		stage('Deploy'){
			steps{
				echo "Deploy Stage"
			}
		}
}
}
	def jarname(){
		
		if(env.BRANCH_NAME == "test"){
			def project = new XmlSlurper().parse(new File("pom.xml"))
			temp = project.build.finalName[0].value
			project.build.finalName[0].value = temp + 'test'+ ${VERSION}
			def pom = groovy.xml.XmlUtil.serialize(xml)
			env.PIPELINE_NAME = project.build.finalName[0].value
		}
			
			
		

	}
