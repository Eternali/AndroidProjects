#!/usr/bin/python3

import os

projects = list(filter(None, os.popen('cd .. && ls').read().strip(' ').split('\n')))
for p in projects:
    if p == 'Src' or p == 'Examples':
        projects.remove(p)

os.system('git pull origin master')

for p in projects:
    os.system('cp -r %s/src $HOME/AndroidStudioProjects/%s/app/' % (p, p))

