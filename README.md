# Livejournal Downloader
Java application to download Livejournal blogs automatically with few options of saving and parsing

## Why people need it
Everyone hates LJ, but long ago it was so popular that many people made a blog there. With this program you can quickly grab your blog, save it to disk and finally delete it from this messy nazi-trolls trashbin called LJ.

## What it can do right now
![myImage](https://github.com/Hexronimo/livejournal-downloader/raw/master/lj-downloader-gui.png)

Only one download option is available for now - download of full pages without `<head></head>` (without style and scripts), all images will be saved locally and their URLs in the pages will be replaced with local ones, so you can collect images with it too.
Also you can choose how much posts you want to download.

## What will be implemented soon
* GUI - done!
* Form validation - half done!
* Command line support - done!
* Options to download just images - done!, full pages with its CSS style
* Options to get the list of posts titles first then to choose which one to download
* Log
* More tests with different kinds of journals

## Contributions


## WARNING
I have no idea if LJ can ban you for doing such downloads, so don't over-use it (like don't put too many threads in the parser, I don't know... just be aware))
