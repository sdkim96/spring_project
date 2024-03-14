import React, { useState } from "react";
import { useNavigate } from 'react-router-dom';
import '../css/loginpage.css';

const LoginPage = ({isLoggedIn, setIsLoggedIn}) => {

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const [errorMessage, setErrorMessage] = useState(null); // 에러메세지 렌더링
    const navigate = useNavigate(); // 홈페이지로 리다이렉트

    const handleMemberSubmit = async (e) => {
        e.preventDefault();

        try {
          const response = await fetch('http://localhost:8080/member/login', {
              method: 'POST',
              headers: {
                  'Content-Type': 'application/json',
              },
              body: JSON.stringify({ email, password })
          });
      
          const data = await response.json(); // Parsing response body for other data
          console.log(data);
      
          // Extracting the JWT from the Authorization header
          const authHeader = response.headers.get('Authorization');
          console.log(authHeader);
          const token = authHeader ? authHeader.split(' ')[1] : null;
      
          if (token) {
              console.log("홈페이지로 리다이렉트")
              setErrorMessage(null);
              localStorage.setItem("token", token); // Storing the token in localStorage
              setIsLoggedIn(true);
              console.log(data.role)

              navigate('/');
          }
      
          if (data.error) {
              console.log(data.error);
              setErrorMessage("아이디 or 비밀번호를 확인하세요.");
          }
      }
      catch (error) {
          console.error('Error: ', error);
          setErrorMessage("An error occurred.");
      }
    }
      

    return(
        <div className="login-page">
            <div className="login-box">
                <div className="member">
                    <form name='login-form' onSubmit={handleMemberSubmit}>
                        <p className="login-text">login</p>
                        {errorMessage && <p className='warning'>{errorMessage}</p>}
                        <label className="member-id">
                            <p>Email</p>
                            <input type="text" name="login-userid" value={email} onChange={e => setEmail(e.target.value)} />
                        </label>
                        <label className="member-pw">
                            <p>Password</p>
                            <input type="password" name="login-password" value={password} onChange={e => setPassword(e.target.value)} />
                        </label>
                        <label className="find-join">
                            <p><a href='/join'>join us</a></p>
                            <p><a href='/search'>search ID or PW</a></p>
                        </label>
                        <button type="submit">login</button>
                    </form>
                </div>
                <div className="social-member">
                    <form name='social-form'>
                        <p className="login-text">소셜 로그인</p>
                        <label className="google-member-login">
                          <a href="http://localhost:8080/oauth2/authorization/google">google login</a>
                        </label>
                    </form>
                </div>
            </div>
        </div>
    )
}

export default LoginPage;