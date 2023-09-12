배포 전략
===
다음과 같이 브랜치를 분리한다.

* main

개발 브랜치. rms-test namespace에 배포한다. 수정된 코드를 push하면 테스트 후 바로 배포한다. deploy를 통해 릴리즈 후에는 deploy 브랜치로 리베이스한다.
ArgoCD로 헬름차트 배포 시 Values.host 에 rms-test.apps.gcgenome.com 값을 지정한다.

* deploy

운영 브랜치. rms namespace에 배포한다. main 브랜치의 변경점을 merge 함으로써 배포가 시작된다.
ArgoCD로 헬름차트 배포 시 Values.host 에 request.gcgenome.com 값을 지정한다.

postgresql-ha
===
oc adm policy add-scc-to-user anyuid -z postgres -n 네임스페이스
oc adm policy add-scc-to-user privileged -z postgres -n 네임스페이스
