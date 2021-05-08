#!/bin/bash
echo "Enter commit message:"
# shellcheck disable=SC2162
read commit_message
echo "The message is: $commit_message"
echo "======START COMMIT========"
# your api module
api_module="market-api"
# your service module
service_module="market-service-cecloud-com"
# shellcheck disable=SC2207
# shellcheck disable=SC2006
# shellcheck disable=SC2116
priority_modules=(`echo ${api_module}` `echo ${service_module}`)
# shellcheck disable=SC2207
# shellcheck disable=SC2006
# shellcheck disable=SC2116
full=($(find . -type d -maxdepth  1  ! \( -name "`echo ${api_module}`" -o -name "`echo ${service_module}`" -o -name ".*" \) -exec ls -d {} \;))
# shellcheck disable=SC2206
processed_modules=(${priority_modules[@]} ${full[*]})
# shellcheck disable=SC2068
for file in ${processed_modules[@]};
do
if [ -d "$file" ]
then 
  # shellcheck disable=SC2046
  # shellcheck disable=SC2164
  # shellcheck disable=SC2006
  # shellcheck disable=SC2116
  cd `echo "$file"`
  # shellcheck disable=SC2006
  current_branch=`git symbolic-ref --short -q HEAD`
  echo "→ → → 【 $file 】at $current_branch"
  # git config core.ignorecase false
  git add .
  # shellcheck disable=SC2006
  # shellcheck disable=SC2116
  git commit  -m "`echo "${commit_message}"`"
  echo "→ → → pulling from $current_branch"
  git pull origin "${current_branch}"
  # shellcheck disable=SC2028
  echo '\n'
  cd ..
fi
done
echo "======COMMIT OVER========"