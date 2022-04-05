
# ![cli icon](https://marcguillem.dev/img/cli.png) AWS JSON TRANSLATOR CLI
**AWS JSON TRANSLATOR CLI** is a command line application to **translate JSON files using AWS Translate**.

# Table of contents
- [🔧 Features](#-features)
- [💻 Usage](#-usage)
- [👩‍💻 Credits and used technologies](#-credits-and-used-technologies)
- [💼 License](#-license)

## 🔧 Features
- 📄 **Deep and non-deep** JSON Files (support for **JSON Arrays too**)
- 👔 Support for **formality and profanity** (to view supported languages see the links below)
- 🔑 **Easy configuration of keys and region**  with a "_**set-configuration**_" command.
- 📃 Translate **without altering the structure**.

## 💻 Usage
### Create an IAM account with AWS Translation:

- Login on AWS console and navigate to [**IAM panel**](https://console.aws.amazon.com/iam/home)
- Click on **`Users`** tab
- Click on **`Add User`** button
- Follow the steps and add a **`User`** with access to the Translation API only
- Save the **`access key`** and the **`secret`** in a secure place

**Available regions**: [AWS Regions documentation](https://docs.aws.amazon.com/directoryservice/latest/admin-guide/regions.html)
With both keys and region, we are ready to do the configuration.

### Running configuration

1. **Configure `Access Key, Secret Key and Region`** using **`"set-configuration"`** command **specifying parameters**.

    - `--awsAccessKey`.  **_OPTIONAL_.** Your AWS Access Key from IAM User.
    - `--awsSecretKey`.  **_OPTIONAL_.** Your AWS Secret Key from IAM User.
    - `--awsRegion`.  **_OPTIONAL_.** Your AWS Region.

_All parameters are optional, so you can set individually each property if you want._

#### Run .exe
 ```properties
.\aws-json-translator.exe set-configuration --awsAccessKey exampleAccessKey --awsSecretKey exampleSecretKey --awsRegion eu-west-3
```

#### Run .jar
 ```properties
java -jar aws-json-translator.jar set-configuration --awsAccessKey exampleAccessKey --awsSecretKey exampleSecretKey --awsRegion eu-west-3
```

2. **Translate JSON file** using **`"translate-json"`** command **specifying required parameters**.

    - `-sl` or `--sourceLanguage`.  **_REQUIRED._** Source language of JSON in _ISO 3166 alpha-2 code_.
    - `-tl` or `--targetLanguage`. **_REQUIRED._** Target language you want to translate to in ISO 3166 alpha-2 code.
    - `-sf` or `--sourceFile`.  **_REQUIRED._** Source file containing JSON to translate.
    - `--enableProfanityFilter`. **_OPTIONAL._** Enable profanity filter. **(Empty parameter)**
    - `--formality`. **_OPTIONAL._** Formality level. Possible values: formal, informal. Default: formal
    - `--customTerminology`. **_OPTIONAL._** Custom terminology name list separated by **comma**.

_To use custom terminology, you have to create a custom terminology in Amazon Translate and then put the name in `--customTerminology` parameter. For more information, see [Amazon Translate Custom Terminology](https://eu-west-3.console.aws.amazon.com/translate/home?region=eu-west-3#terminology)._

### Basic usage of `"translate-json"`

---
#### Run .exe
 ```properties
.\aws-json-translator.exe translate-json -sf example.json -sl es -tl en
```

#### Run .jar
 ```properties
java -jar aws-json-translator.jar translate-json -sf example.json -sl es -tl en
```

_This command will translate the file `example.json` from Spanish to English_

### Extended usage of `"translate-json"`

---
#### Run .exe
 ```properties
.\aws-json-translator.exe translate-json --sourceFile example.json --sourceLanguage es --targetLanguage en --formality formal --enableProfanityFilter --customTerminology list1,list2
```

#### Run .jar
 ```properties
java -jar aws-json-translator.jar translate-json --sourceFile example.json --sourceLanguage es --targetLanguage en --formality formal --enableProfanityFilter --customTerminology list1,list2
```

_This command will translate the file `example.json` from Spanish to English with formality `formal`, with enabled `profanity filter` and a list of custom terminologies_

3. **Done!** Your translated JSON is on _`.\output`_ folder with the target language code as name.

# 👩‍💻 Credits and used technologies
- [Java 11](https://www.oracle.com/es/java/technologies/javase/jdk11-archive-downloads.html)
- [Gradle](https://gradle.org/)
- [PicoCLI](https://mvnrepository.com/artifact/info.picocli/picocli)
- [Launch4J](http://launch4j.sourceforge.net/)
- [Apache Commons IO](https://mvnrepository.com/artifact/commons-io/commons-io)
- [JSON In Java](https://mvnrepository.com/artifact/org.json/json)
- [AWS Translate Java SDK](https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk-translate)
- [Github Copilot](https://copilot.github.com/)
-  <a target="_blank" href="https://icons8.com/icon/19292/consola">Consola</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a>
- <small><i><a href='http://ecotrust-canada.github.io/markdown-toc/'>Table of contents generated with markdown-toc</a></i></small>


# 💼 License
MIT License

Copyright (c) 2022 Marc Guillem Dev

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.