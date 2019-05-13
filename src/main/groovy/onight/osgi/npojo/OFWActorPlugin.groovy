package onight.osgi.npojo

import org.gradle.api.Plugin
import org.gradle.api.Project


class OFWActorPlugin implements Plugin<Project> {

	void apply(Project target) {
		//target.dependencies()
		
		
		target.task('buildbundle',type:BundleTask)
		
	
		//target.buildscript.dependencies.add(
		if(target.hasProperty('repos_host')){
			target.repositories {
				maven {url target.repos_host }
			}
			
			target.buildscript {
				repositories { maven { url target.repos_host
					} }
				//dependencies { classpath 'com.google.protobuf:protobuf-gradle-plugin:0.8.1' }
					
				//dependencies { classpath "onight.osgi:npojo:1.3.0" }
			}
		}
		//println target.buildscript.dependencies
		//target.buildscript.dependencies.add("classpath",'com.google.protobuf:protobuf-gradle-plugin:0.8.1');
		
		
		target.project.apply([ plugin: "maven"]);
		target.project.apply([ plugin: "osgi"]);
		target.project.apply([ plugin: 'java']);
		target.project.apply([ plugin: 'eclipse']);
		target.project.apply([ plugin: 'scala']);
		target.repositories {
			maven { url "https://plugins.gradle.org/m2/" }
		}
		
		target.plugins.with {
            apply com.google.protobuf.gradle.ProtobufPlugin
        }
		target.project.apply([ plugin: 'com.google.protobuf']);

		
		target.protobuf { protoc{ artifact = 'com.google.protobuf:protoc:3.6.1' } }

		target.sourceSets.main
		{
			proto {
			// In addition to the default 'src/main/proto'
				srcDir 'src/gens/proto'
			}
			
			java {
				srcDir 'build/generated/source/proto/main/java'
				srcDir 'src/main/proto'
				srcDir 'src/main/java'
				srcDir 'src/main/resources'
				srcDir 'src/gens/java'
				srcDir 'src/gens/proto'
			}
			resources {
				srcDir 'src/resources'
				srcDir 'src/gens/resources'
			}
		}
		
		target.configurations {
			includeInJar
			deployerJars
		}

		target.project.sourceCompatibility = 1.8
		target.project.targetCompatibility = 1.8

		target.compileJava.options.encoding = 'UTF-8'

		target.dependencies {
			compile 'org.apache.felix:org.apache.felix.ipojo.manipulator:1.12.1'
			compile 'org.apache.felix:org.apache.felix.ipojo.annotations:1.12.1'
			compile 'org.apache.felix:org.apache.felix.ipojo.api:1.12.1'
			compile 'org.apache.felix:org.apache.felix.framework:6.0.1'
			compile 'org.apache.felix:org.apache.felix.main:6.0.1'
			compile 'ch.qos.logback:logback-classic:1.2.3'
			includeInJar 'com.googlecode.protobuf-java-format:protobuf-java-format:1.4'
			compile 'org.apache.avro:avro:1.8.2'
			compile "org.apache.commons:commons-lang3:3.8.1"
			compile 'org.scala-lang:scala-library:2.12.3'
			compile 'com.google.protobuf:protobuf-java:3.6.1'
			includeInJar  'org.projectlombok:lombok:1.18.4'
			compile 'org.ow2.spec.osgi:ow2-httpservice-1.2-spec:1.0.0'
			testCompile 'junit:junit:4.12'
			compile 'org.slf4j:slf4j-api:1.7.25'
			compile 'commons-codec:commons-codec:1.11'
				
				//!!!
			compile 'onight.osgi:zpp-gradle_1.8:3.3.0'
			
			target.configurations.compile.extendsFrom(target.configurations.includeInJar)
		}
		
		
		target.configurations {
			all*.exclude module: 'org.scala-lang.scala-library'
		}
		//add ignore
		target.jar {
			exclude('*scala-library*')
		
			manifest{
				attributes( 'Import-Ignore-ZP':'com.googlecode.protobuf,com.google.protobuf,com.esotericsoftware.kryo,jnr.posix,org.apache.avro')
				attributes( 'DynamicImport-Package': '*')
				attributes( 'Import-Lib': 'lib')
			}
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
