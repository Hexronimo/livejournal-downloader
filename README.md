# Livejournal Downloader
Java application to download Livejournal blogs automatically with few options of saving and parsing

## What it can do right now
For now, you can write options only directly to Main class and then compile and run it by yourself. 
Only one download option available for now - download of full pages without <head></head> (without style and scripts), all images will be saved locally
and their URLs in the pages will be replaced with local ones, so you can collect images with it too.
Also you can choose how much posts you want to download.

## What will be implemented soon
* GUI
* Command line interaction
* options to download just images, full pages with its CSS style
* options to get the list of posts titles first then to choose which one to download

## WARNING
I have no idea if LJ can ban you for doing such downloads, so don't over-use it (like don't put too many threads in the parser, I don't know... just be aware))
