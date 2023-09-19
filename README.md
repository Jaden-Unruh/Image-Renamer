# Image Renaming Tool
A tool to rename many image files all at once
## Setup
Java SE 17 is required to run this program. If you've run any of my previous tools, it should already be installed.

If you don't have Java installed, you can download an installer for Temurin/OpenJDK 17 from [here](https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.8%2B7/OpenJDK17U-jdk_x64_windows_hotspot_17.0.8_7.msi). This is an open-source version of java. Once downloaded, you can run the installer by double-clicking, it will open a window guiding you through the installation. Leaving everything as the defaults and just clicking through the pages should work perfectly.

If you're not sure if you have Java installed correctly, you can check by doing the following:
1. Press Win+R, type `cmd` and press enter - this will open a command prompt window
2. Type `java -version` and press enter
3. If you've installed java as specified, the first line under your typing should read `openjdk version "17.0.8" 2023-07-18`[^1]. If, instead, it says `'java' is not recognized as an internal...` then java is not installed, see the above paragraph.

[^1]: If this shows a higher number, things should all work the same. If it shows a lower number, the script might work, but if things go wrong try updating to this version. The same installer from above should do that.

The `.jar` (executable java file, the application itself) for the program is located within the [GitHub](https://github.com/Jaden-Unruh/Image-Renamer) (you should be here now, or downloaded this file from there). It's called `Image Renamer.jar`. You can download it by selecting that file from the github page, then clicking the 'Download raw file' icon in the top right (an arrow facing downwards towards a tray).

Once java has been installed and the jar downloaded, double click the jar file to run the script.
## GUI and how to use
When run, the application will open a small window titled "Photo Renaming Tool". It will have five lines of input above two buttons, as follows:
* Select an Input directory: `Select...`
	- Click the `Select...` button and it will open a file selection prompt. Navigate to the directory that contains all of the photos you wish to rename, as specified in the [File Structure](/#File-Structure) section.
* Select an Output directory: `Select...`
	- Click the `Select...` button and it will open a file selection prompt. Navigate to the directory you would like to use as an output folder. Nothing within this folder will be deleted, but running the script multiple times will create numbered duplicates of photos.
* Enter the site ID:
	- Type the site id of the photos you would like to rename. As it is, the script can only do one site/one location at a time. However, once it finishes with one site, you can keep it open, change the inputs, and run it again.
* Enter the location number:
	- Type the location number of the photos you would like to rename.
* Enter the data year:
	- Enter the year of the photos you would like to rename.
* `Close` and `Run`
	- `Close` will stop and close the application. It should still work while the program is running, but doing so may result in an incompletely copied (and thus corrupted) image.
	- `Run` will start the program, if all data is properly selected. Otherwise, it will show text below these buttons prompting inputs. The text inputs (site id, location number, and year) will show red text if they are not properly formatted.
## Troubleshooting
> Nothing's happening when I double click the `.JAR` file

Ensure you've installed Java as specified under [Setup](/#Setup). If you believe you have, try checking your java version as specified above.

---
> `Run` isn't doing anything

Ensure you've selected directories and typed in data as prompted. If any of the text entry fields are in red text, that means your formatting is wrong. You can delete your text and click out of the field to see the correct formatting.

---
> Some photos are being skipped

Photos that are not named and within directories as stated under [File Structure](/#File-Structure) will not be copied. Any that are skipped should be mentioned within the `rename-info` document after the script is complete.[^2]

[^2]: The only other reason a file would be skipped is if it wasn't recognized as an image. The script will recognize a file as an image if it has one of the following extensions: .jpg, .jpeg, .jpe, .jif, .jfif, .jfi, .png, .gif, .webp, .tiff, .tif, .psd, .raw, .arw, .cr2, .nrw, .k25, .bmp, .dib, .heif, .heic, .ind, .indd, .indt, .jp2, .j2k, .jpf, .jpx, .jpm, .mj2, .svg, .svgz, .ai, .eps (yes, I just looked up a list of common image file extensions) -- note that the file extensions are converted to lowercase before being checked against this list, so capitalization does not matter. The file's extension will be maintained when it is copied. Any files that do not have an extension listed above will not be copied/renamed and will not be noted in the `rename-info` file. This list of acceptable file extensions can be changed without recompiling the project - they are stored in `ImageExtensions.dat` directly within the `.jar` - open the `.jar` with a tool like [WinRAR](https://www.win-rar.com/download.html), edit the file with a text editor like Notepad, then recompress the `.jar` with WinRAR, and everything should work well.

---
> What's this `rename-info` file?

Every time you run the script, it will generate a rename-info file with the date and time that you ran it. Within this file, any images that are skipped and any duplicate outputs will be noted, so that you can manually check these. Essentially, it's the scripts way of saying it may have made a mistake, or that it believes a file was incorrectly named or sorted.

## File Structure
Below is an example of a file structure you could run the script on (the lines marked by a '*' have errors):

* INPUT DIRECTORY
	- AB100001 - a name - 3
		+ E.jpeg
		+ S.png
		+ W.gif
		+ N.webp
	- Subdirectory
		+ AB100002 - a different name - 4 - ELEVATIONS
			* Photos
				- N.jp2
				- E.jif
				- S.svg
				- W.raw
		+ AB100003 - another name - 5A
			* N.tiff
			* E.bmp
			* S.heic
			* W.jpg
	- AB100004-name-23  *
		+ IMG_3040.jpeg *
		+ IMG_3041.jpeg *
		+ IMG_3042.jpeg *
		+ IMG_3043.jpeg *

Every image should be named with a single character representing a cardinal direction (`N`, `E`, `S`, or `W`). Somewhere above it in the file tree should be a folder named exactly as follows: `{Maximo id} - {building name} - {building number}`. This may be followed by ` - ELEVATIONS`, in which case the images will be renamed accordingly. The regular expression used to capture this information is: `(AB\d{6})\s+-\s+(.+?)\s+-\s+(.+?)(?:\s+-\s+ELEVATIONS)?\\`, run with an input of the file's absolute path.

## But what does it do?
First, the script will do some initialization steps, like checking the inputs and creating the `rename-info` file. Then, it will search through the input directory and all its sub-directories. If it finds any image file, it will attempt to find all of the information it needs as specified in [File Structure](#/File-Structure). If it does, it then copies the file, renamed, to the output directory. If not, it makes a note in `rename-info`. If a file by the same name it's trying to rename to already exists, it will add a number in parentheses to the end of the name, and make a note in `rename-info`.

## In the [GitHub](https://github.com/Jaden-Unruh/Image-Renamer)
This repository is the project folder on my computer - this is so anyone trying to edit, change, or otherwise do anything with the code in the future can do so relatively easily.

There should be a `.jar` file directly in the repository - that's all you need to run the program. If it's not there, check inside the `target` folder - that's where it gets generated when I compile the code. This `.jar` file is an executable file that contains all of the java code I wrote, all the resource files, and all the dependency files bundled neatly into one. It can be decompressed using a tool like WINRAR, to edit certain pieces, but not decompiled to view the code itself - this is why I also added all the project files to the github, decompiled and editable.

The files named `README.md` and `README.html` have the same contents, just in two different file formats. Use the .md file if you can open it, it might be formatted slightly better. Otherwise, you should be able to open the .html file on any device.

The rest of the files in the github (within the main directory, that should be `.settings`, `doc`, `src`, `target`, `.classpath`, `.project`, and `pom.xml`) are project files - everything that gets put into the `.jar` file, or is used in creating the `.jar` file. If you change anything within the java code (located at `src\main\java\us\akana\tools\photo_renamer\`) or any other element, be sure to recompile the code before running again. I suggest doing this with Maven, as the `pom.xml` file will ensure all dependencies are included and the manifest gets generated correctly. To do this, open a terminal in the directory that contains `pom.xml`and all the other project folders and run `mvn clean compile assembly:single` (you must have [Maven](https://maven.apache.org/install.html) installed). The new `.jar` will be generated in the `target` directory.