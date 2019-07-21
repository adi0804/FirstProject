#!/usr/bin/env groovy
pipeline {
	agent any
	 environment {
    //Use Pipeline Utility Steps plugin to read information from pom.xml into env variables
    IMAGE = readMavenPom().getArtifactId()
    VERSION = readMavenPom().getVersion()
    NAME = readMavenPom().getfinalName()
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
				echo "${NAME}"
			}
		}
		stage('Deploy'){
			steps{
				echo "Deploy Stage"
			}
		}
}
}
