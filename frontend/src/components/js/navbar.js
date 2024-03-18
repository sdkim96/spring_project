import { useState, useEffect } from "react";
import '../css/navbar.css'
import { NavLink } from 'react-router-dom';
import { useCookies } from 'react-cookie';
import { useNavigate } from 'react-router-dom';

const NavBar = ({isLoggedIn, setIsLoggedIn}) =>{

    const [word, setWord] = useState('Didim365');
    const [fade, setFade] = useState(false);
    const [isKorean, setIsKorean] = useState(false);  // 새로운 상태 추가
    const [cookies, setCookie, removeCookie] = useCookies(['token']);
    const navigate = useNavigate();

    
    const words = (latin, korean, mouseover) => {
        if (mouseover) {
            setFade(true);
            setTimeout(() => {
                setWord(korean);
                setFade(false);
                setIsKorean(true);  // 한국어 문장 출력 시 상태 업데이트
            }, 500);
        } else {
            setFade(true);
            setTimeout(() => {
                setWord(latin);
                setFade(false);
                setIsKorean(false);  // 라틴어 문장 출력 시 상태 업데이트
            }, 500);
        }
    }

    const handleLogout = async (e) => {
        e.preventDefault();
        const tokenFromLocalStorage = localStorage.getItem('token');
        const tokenFromCookies = cookies.token;

        console.log("tokenFromLocalStorage:", tokenFromLocalStorage)
        console.log("tokenFromCookies:", tokenFromCookies)

        let token, provider;
        if (tokenFromLocalStorage) {
            token = tokenFromLocalStorage;
            provider = "web1"; 
        } else if (tokenFromCookies) {
            token = tokenFromCookies;
            provider = "google"; 
        }

        console.log(token, provider);
    
        if (token) {
            try {
                const response = await fetch('http://localhost:8080/member/logouts', {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ "provider" : provider }) 
                });

                const data = await response.json();
                console.log(data);
                if (response.ok) {
                    console.log('Logout successful');
                    setIsLoggedIn(false); 
                    localStorage.removeItem('token'); 
                    removeCookie('token');
                    navigate ('/'); 
                } else {
                    console.log('Logout failed');
                }

                
                
            } catch (error) {
                console.error('An error occurred:', error);
            }
        }
    }
    
        

    // authorize url로 접속하려는 사이트 정보, 인증 토큰 보내야됨
    const handleStaff = async () => {
        const token = localStorage.getItem('token');
        const url = 'to_admin_url'

        console.log(token)
        

        try{
            const response = await fetch('http://localhost:8000/myapp/authorize/', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,  
                    'Content-Type': 'application/json'
                }, 
                body: JSON.stringify({ 'work': url })  // body에 JSON 형태로 데이터를 전달합니다.
            });
            
            if (!response.ok) {
                throw new Error('당신의 인증토큰이 만료되었습니다.');
            }
        
            const data = await response.json();
    
            if (data.result) {
                window.location.href = 'http://localhost:3000/admin/';
            } else {
                window.location.href = 'http://localhost:3000/noauthorize/';
            }

        } catch (error) {
            console.error('An error occurred:', error);
        }
    }

    return(
        <nav id='site-navbar'>
            <div className='menu-toggle'>
                <ul>
                    {
                        isLoggedIn
                        ? <>
                            {/* { isStaff 
                                ? <li><a href="#" onClick={handleStaff}>admin</a></li>
                                : <li><a href="#" onClick={handleMypage}>mypage</a></li> 
                            } */}
                            <li><a href="/" onClick={handleLogout}>logout</a></li>
                            <li><NavLink to = "/mypage">mypage</NavLink></li>
                            <li><a href="/" onClick={handleStaff}>staff</a></li>
                        </>
                        : <li><NavLink to="/login">login</NavLink></li>
                    }
                </ul>
            </div>   
            <div className="logo-words">
                <div className='logo-container'>
                    <p className='logo'><a href='/'>스프링 기반 도로명주소 찾기 서비스</a></p>
                </div>
                <div className="word">
                    <p 
                        className={`words ${fade ? 'fade' : ''} ${isKorean ? 'korean' : ''}`}
                        onMouseOver={() => words('Didim365', 'By AI MSP 기술센터 김성동 사원', true)}
                        onMouseOut={() => words('Didim365', 'By AI MSP 기술센터 김성동 사원', false)}
                    >
                        {word}
                    </p>
                </div>
            </div>
        </nav>
    )
}

export default NavBar;