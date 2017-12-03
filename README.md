# Download songs from kannadaaudio.com
## Introduction
This is a private project to develop a Java based application to download songs from 
kannadaaudio.com website. This website contains a lot of audio content from famous artists.
It is next to impossible to download all the song manually. Hence the project. 

This application downloads all the songs and image in all the album urls specified into 
corresponding directories with proper file names for songs as specified in the website 
and not as present in the url fine name.

Also this is an attempt to learn Java. I am happy that I could do this (my first full project) within a day.
## Requirements
### Functional Specifications
The application should be able to perform as below specifications
- Take the list of urls of kannadaaudio.com song albums
- A specific directory has to be created for each album with name same as album title
- Download the all the .rm files of each album and save it to its specific directory
- File names has to be same as song title in web page
- Download the album art to the specific directory
- The urls of those albums which failed should be listed in a file.
- It should be possible to
    - Provide kannadaaudio.com album url list through a file
    - Provide directory to download
    - Provide error file path
    
### Non-functional specifications
- It can use all the network, memory and CPU to process.
- Downloads can be parallel.


## Usage instructions
Use below overrides to specify the paths. The values shown are defaults.
`` 
ka.details.urlsFilePath=d:\\kannada-audio\\urls.txt

ka.details.urlErrorsFileName=error-urls.txt

ka.details.baseDownloadDirPath=d:\\kannada-audio
``


