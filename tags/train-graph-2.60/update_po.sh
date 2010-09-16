#! /bin/bash
xgettext --from-code=UTF-8 -k_ -o po/messages.pot `find . -name "*.java"`
msgmerge -U po/zh_CN.po po/messages.pot
msgmerge -U po/en_US.po po/messages.pot