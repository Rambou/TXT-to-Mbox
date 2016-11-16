# TXT-to-Mbox
Converting txt files into a unique mbox file.

Story: I was trying to find an application to convert my .txt mail files into .mbox, which i had generated them using Thunderbird plugin "ImportExportTools", and i coundn't find one. So i created this programm using Java JDK8 and netbeans as an IDE that gets all .txt files and put their content into an .mbox file.

That's all folks, if anyone is interested feel free to contribute.

Using: java -jar txt_to_mbox.jar FOLDER_OF_TXT OPTIONAL_NAME_OF_MBOX

This implementation is made using this [RFC](https://tools.ietf.org/html/rfc4155) and Gmail/Thunderbird mbox examples in mind. If you're about to use this script with any other mbox file viewers of exporters you should keep in mind that there is possibility they use their own implementation of mbox. So to be clear you may have to tweak this script in order to make it work for the specific viewer/exporter app. 

Please read more in this [wiki](https://en.wikipedia.org/wiki/Mbox).
