Image Tagger
------------

###Features 

* Tag images sequentially using a (mostly)CLI-like interface. 
  
  Currently uses an internal database instead of tagging files themselves.

* Search through tags using flexible search queries.

  Currently only supports simple, linear searches of one or more tags. 

###Configuration

Image Tagger _must_ be configured before use. The location of the configuration file is $HOME/.config/image\_tagger/config on Linux and $HOME/.image\_tagger/config.txt on Windows/Mac. 

Example configuration file contents:

```
source = /home/dash/images
dest = /home/dash/images/tagged
```

This would tell Image Tagger to look in /home/dash/images by default for images to tag, and to move all tagged images to /home/dash/images/tagged after they've been tagged.

###Commands

Image Tagger supports CLI-like commands for quick tagging and searching. Commands are interpreted relative to the mode you're currently in.

##Tag Mode
* add tag \<tag name without spaces\>
  Adds a tag to Image Tagger's database so that it may be used to tag images.
* add alias \<tag\> \<space-separated aliases\>
  Adds aliases \<space-separated aliases\> to \<tag\>. Whenever any of the specified aliases are used to tag or search, they will be interpreted as if you used \<tag\>.
* \<tag\>
  Tags the currently displayed image with \<tag\>. More than one tag may be added to an image at once.
* skip
  Skips the currently displayed image without tagging or moving it and displays the next one in the image source directory. 

##Search Mode
* \<tag\>
  Displays all images tagged with \<tag\>. More than one tag can be used in a single search query. 
* tag 
  Initiates tag mode. Tag mode sequentially displays all images in the image source directory, one image at a time, so they may be tagged.
