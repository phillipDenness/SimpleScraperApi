#! /bin/bash

echo "checking chromium instances"
chromiumInstances=`ps aux --sort=lstart | grep "${CHROME_BINARY}" | grep 'zygote' | grep -v 'grep' | wc -l`

if [[ $chromiumInstances -gt 1 ]]; then
    oldestInstance=`ps aux --sort=lstart | grep "${CHROME_BINARY}" | grep 'zygote' | grep -v 'grep' | head -n 1`
    pidToKill=`echo $oldestInstance | awk '{print $2}'`

    echo "Killing chromium process id "$pidToKill
    sudo kill -9 $pidToKill
fi
