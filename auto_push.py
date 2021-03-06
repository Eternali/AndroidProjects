#!/usr/bin/python3

import os
import sys

projects = list(filter(None, os.popen('cd .. && ls').read().strip(' ').split('\n')))
for p in projects:
    if p == 'Src' or p == 'Examples' or p == 'WAToPlan':
        projects.remove(p)

for p in projects:
    #os.system('rm -r $HOME/AndroidStudioProjects/Src/%s/*' % p)
    #os.system('cp -r $HOME/AndroidStudioProjects/%s/app/src $HOME/AndroidStudioProjects/Src/%s/' % (p, p))
    os.system('rsync -avz --delete "$HOME/AndroidStudioProjects/%s/app/src" "$HOME/AndroidStudioProjects/Src/%s"' % (p, p))

os.system('git add -f .')
commit_msg = input('Enter commit message: ')
os.system('git commit -am "%s"' % commit_msg)
os.system('git push {} origin master'.format('--force' if 'force' in sys.argv[-1].strip() else ''))

