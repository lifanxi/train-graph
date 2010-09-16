#! /bin/bash

for file in po/*.po ; do
	echo "Converting language file: " $file
	msgfmt --java2 -d $1 -r resources.Messages -l `echo $file | cut -f 1 -d '.' | cut -f 2 -d '/'` $file
done

