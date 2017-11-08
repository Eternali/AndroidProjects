#!/usr/bin/python3

import os

projects = list(filter(None, os.popen('cd .. && ls').read().strip(' ').split('\n')))
for p in projects:
    if p == 'Src' or p == 'Examples' or p == 'WAToPlan':
        projects.remove(p)

os.system('git pull origin master')

for p in projects:
    #os.system("rm -r $HOME/AndroidStudioProjects/%s/app/src/*" % p)
    #os.system('cp -r %s/src/* $HOME/AndroidStudioProjects/%s/app/src/' % (p, p))
    os.system('rsync -avz --delete "$HOME/AndroidStudioProjects/Src/%s/src" "$HOME/AndroidStudioProjects/%s/app"' % (p, p))

