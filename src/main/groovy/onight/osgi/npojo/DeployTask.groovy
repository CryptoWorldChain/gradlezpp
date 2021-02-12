package onight.osgi.npojo

import onight.osgi.ipojo.ExtClassLoader
import onight.osgi.ipojo.ExtPojoization
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files
import java.nio.file.StandardCopyOption

class DeployTask extends DefaultTask {

	DeployTask() {
		group = "build"
	}

	@TaskAction
	def buildbundle() {
		if(project.hasProperty('deploy_path')){
			File jarfile = project.file(project.jar.archivePath)
			println "deploy.From:"+project.jar.archivePath+":"+new Date(jarfile.lastModified()).format("YYYY-MM-DD HH:mm:ss")
			File targetJarFile = project.file(project.getProperty('deploy_path') +"/" + project.jar.baseName +"-"+project.version+".jar" )
			Files.copy(jarfile.toPath(),targetJarFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
//			jarfile.copyTo(targetJarFile)
			println "deploy.TO:"+targetJarFile.absolutePath+":"+new Date(targetJarFile.lastModified()).format("YYYY-MM-DD HH:mm:ss")

		}else{
			println "cannot find property deploy_path"
		}

	}
}
