# :construction_worker: Pedreiro - A declarative scaffolding tool 

## Blueprints

Pedreiro uses Blueprints declared in YAML to create resources like folders, files, and to execute shell commands. 

This is a example of starting a simple Kotlin project with gradle.

```yaml
---
- type: folder
  name: {{ project_name }}
  children:
    - type: folder
      name: src
      children:
        - type: folder
          name: main
          children:
            - type: folder
              name: kotlin

            - type: folder
              name: resources

        - type: folder
          name: test
          children:
            - type: folder
              name: kotlin

            - type: folder
              name: resources

    - type: file
      name: build.gradle
      content: |-
        plugins {
            id 'org.jetbrains.kotlin.jvm' version '1.3.71'
        }

        group '{{ project_group }}'
        version '1.0-SNAPSHOT' 

        dependencies {
            implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
            
            testImplementation "org.junit.jupiter:junit-jupiter:5.6.1"
        }
        
        compileKotlin {
            kotlinOptions.jvmTarget = "1.8"
        }
        compileTestKotlin {
            kotlinOptions.jvmTarget = "1.8"
        }

  
    - type: command
      command: gradle wrapper

    - type: command
      command: git init
    - type: command
      command: git add .
    - type: command
      command: git commit -m "Initial Commit"
```

And finally we can run the template with the command:

```shell script
pedreiro kotlin-gradle -a "project_name=kata" -a "project_group=com.github.andre2w"
```

### Blueprint building blocks

The blueprint requires a list of resources in the root. 

Right now there are three types supported `folder`, `file`, `command`.

#### Folder

For the folder you must declare the `name`, optionally you can declare the `children` for that folder.
The children will be a list of resources of any type.

```yaml
# Valid folder declarations
- type: folder
  name: pedreiro
  children: 
    - type: command
      command: git init

# This is also valid
- type: folder
  name: pedreiro

# This one is invalid
- type: folder
  children:
    - type: command
      command: git init
      
```

#### File

To create files we must declare the `name` and the `content` of the file. 
In case you need to declare more complex lines you can use the multiline support from yaml. 

```yaml
# Valid declaration with inline content
- type: file
  name: .gitignore
  content: .idea

# Valid declaration with multiline support
- type: file
  name: .gitinore
  content: |-
    .idea
    build/
    out/ 
```

#### Command

To execute shell commands you can use the `command` declaration. 
You can't use pipes or redirect the input, unless you execute a new shell for it. 
The command will be executed inside the folder that is declared. 

```yaml
# Executing a simple command
- type: command
  command: git init

# The command will be executed in the ./pedreiro folder
- type: folder
  name: pedreiro
  children:
    - type: command
      command: touch file.txt
```

#### Variables

You can declare variables in your template so you can set them when calling the blueprint. The blueprint is rendered using 
Handlebars and you declare the variable between double curly braces.

```yaml
- type: folder
  name: "{{ project_name }}"
```

When calling the application you can pass how many values using the `-a` or `--arg` flags. Example:

```shell script
pedreiro test -a "project_name=pedeiro" --arg "group=com.github.andre2w"
```

## Configuration

To application will look for the `configuration.yml` file in a folder called `.pedreiro` in your home dir. 
The configuration file must contain where the application will look for the blueprints. 

Example:

```yaml
# ~/andre/.pedreiro./configuration.yml
---
blueprintsFolder: /Users/andre/.pedreiro/blueprints
```

Right now you must create the folder and add the configuration file there manually.

## Blueprint Folder

If your blueprint file is getting too big you can split into multiple files. For that you must create a folder with 
the name of the blueprint and inside the folder you create the `blueprint.yml` declaring the blueprint.

We can split the previous example to a folder. We create a folder named `folderBlueprint` and create the `blueprint.yml`
inside the folder.

```
blueprintTemplate
└── blueprint.yml
``` 

Now we can change the `blueprint.yml` to reference an external `build.gradle` file instead of having the contents inside
the blueprint. Use the `source` key passing the name of the original file and add the file to the blueprint folder.

```yaml
    - type: file
      name: build.gradle
      source: build.gradle
```

The extra files also support variables like the main blueprint Here's the `build.gradle`: 

```groovy
plugins {
    id 'org.jetbrains.kotlin.jvm' version '1.3.71'
}

group '{{ project_group }}'
version '1.0-SNAPSHOT' 

dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
            
    testImplementation "org.junit.jupiter:junit-jupiter:5.6.1"
}
        
compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}
compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
}
```

The final `blueprint.yml` file will be

```yaml
---
- type: folder
  name: {{ project_name }}
  children:
    - type: folder
      name: src
      children:
        - type: folder
          name: main
          children:
            - type: folder
              name: kotlin

            - type: folder
              name: resources

        - type: folder
          name: test
          children:
            - type: folder
              name: kotlin

            - type: folder
              name: resources

    - type: file
      name: build.gradle
      source: build.gradle
  
    - type: command
      command: gradle wrapper

    - type: command
      command: git init
    - type: command
      command: git add .
    - type: command
      command: git commit -m "Initial Commit"
```

and the folder structure will look like:

```
blueprintTemplate
├── blueprint.yml
├── build.gradle
└── variables.yml
``` 

## Building

### Tests

To run all the tests you can just execute `./gradlew clean check`.

### Fat Jar

To build the project you can clone and execute `./gradlew sJ`, this will generate a fat jar with all the dependencies inside `build/libs`. 
