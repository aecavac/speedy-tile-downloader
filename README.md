speedy-tile-downloader
======================

speedy-tile-downloader is a speedy map tile downloader.It supports Tile Map Services(TMS) like OpenStreetMap services.
It downloads all tiles of the world for defined zoom levels.
If download problems occurs from any reason, run program again and it will be continue from where it left off.

Single thread performance test result :
==============ZOOM 5-5==============
Time : 263461 ms
Img Count : 1024
Avg : 257 ms
=====================================

Multi thread performance test result :
==============ZOOM 5-5==============
Time : 15823 ms
Img Count : 1024
Avg : 15 ms
====================================
==============ZOOM 6-6==============
Time : 37588 ms
Img Count : 4096
Avg : 9 ms
====================================
==============ZOOM 0-8==============
Time : 944767 ms
Img Count : 87381
Avg : 10 ms
====================================
