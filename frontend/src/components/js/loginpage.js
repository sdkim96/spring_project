import React from 'react';
import { useEffect } from 'react';

function LoginPage() {
  // 이벤트 핸들러나 상태 관리 로직이 필요하다면 여기에 추가하세요.



  return (
    <div>
      <form action="/login" method="post">
        <input type="text" name="name" />
        <input type="password" name="password" />
        <button type="submit">제출</button>
      </form>
      <a href="http://localhost:8080/oauth2/authorization/google">google login</a>
      <a href="http://localhost:8080/oauth2/authorization/naver">naver login</a>
      <a href="http://localhost:8080/oauth2/authorization/kakao">kakao login</a>
      <a href="/joinForm">회원가입</a>
    </div>
  );
}

export default LoginPage;
