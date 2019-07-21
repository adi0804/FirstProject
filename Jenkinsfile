#!/usr/bin/env groovy
pipeline {
	agent any
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
    				if (env.BRANCH_NAME != null) {
					echo "${env.BRANCH_NAME}"
   				}
			}
			}
		stage('Test'){
			steps{
				echo "Test Stage"
			}
		}
		stage('Deploy'){
			steps{
				echo "Deploy Stage"
			}
		}
}
}
