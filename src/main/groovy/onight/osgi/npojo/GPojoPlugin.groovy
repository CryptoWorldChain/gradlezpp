package onight.osgi.npojo

import org.gradle.api.Plugin
import org.gradle.api.Project

 
class GPojoPlugin implements Plugin<Project> {

	void apply(Project target) {
		target.dependencies()
		target.task('buildbundle',type:BundleTask)
		
		
		target.project.apply([ plugin: "maven"]);
		target.project.apply([ plugin: "osgi"]);
		target.project.apply([ plugin: 'java']);
		target.project.apply([ plugin: 'eclipse']);
		
		target.configurations {
			includeInJar
			deployerJars
		}

		target.project.sourceCompatibility = 1.8
		target.project.targetCompatibility = 1.8

		target.compileJava.options.encoding = 'UTF-8'
		
		target.dependencies {
			compile 'org.apache.felix:org.apache.felix.ipojo.manipulator:1.12.1'
			compile 'org.javassist:javassist:3.24.0-GA'
			compile   'org.apache.felix:org.apache.felix.ipojo.annotations:1.12.1'
			compile   'org.apache.felix:org.apache.felix.ipojo.api:1.12.1'
			compile   'org.apache.felix:org.apache.felix.framework:6s.0.1'
			compile   'org.apache.felix:org.apache.felix.main:6.0.1'
			deployerJars "org.apache.maven.wagon:wagon-ssh:3.2.0"
			compile   'ch.qos.logback:logback-classic:1.2.3'

			target.configurations.compile.extendsFrom(target.configurations.includeInJar)
		}

		if(target.hasProperty('obr_host')){
			target.uploadArchives  {
				repositories {
					mavenDeployer {
						repository(url: target.obr_host) {
							authentication(userName:target.obr_usr,password:target.obr_pwd)
						}
					}
				}
			}
		}

		//		update.updateRepository();

		target.jar {
			into('lib') { from target.configurations.includeInJar }
			manifest{ instruction 'Export-Package','*' //attributes( 'Bundle-Activator': 'org.nights.stringio.Activator')
			}
		}

		target.jar.doLast{
			target.tasks.buildbundle.execute();
		}
	}

	public String getProp(String key,String defaultv){
		if(System.getProperty(key)!=null) {
			return System.getProperty(key);
		}
		if(System.getenv(key)!=null){
			return System.getenv(key);
		}
		return defaultv;
	}
}
