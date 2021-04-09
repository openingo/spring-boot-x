#!/bin/bash
echo "======START RESET========"
# shellcheck disable=SC2045
# shellcheck disable=SC2006
for file in `ls`;
do
if [ -d "$file" ]
then 
  # shellcheck disable=SC2046
  # shellcheck disable=SC2164
  # shellcheck disable=SC2116
  cd `echo "$file"`
  current_branch=`git symbolic-ref --short -q HEAD`
  echo "→ → → reseting【 $file 】at $current_branch"
  git reset --hard
  # shellcheck disable=SC2028
  echo "\n"
  cd ..
fi
done
echo "======RESET OVER========"