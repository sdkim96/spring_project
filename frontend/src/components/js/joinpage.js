import { useState } from 'react';
import '../css/joinpage.css';
import { useNavigate } from 'react-router-dom';

const JoinPage = () => {

    const [name, setname] = useState("");
    const [password, setPassword] = useState("");
    const [passwordCheck, setPasswordCheck] = useState("");
    const [email, setEmail] = useState("");

    const [problemFill, setProblemFill] = useState("");
    const [problemPw, setProblemPw] = useState("");
    const [problemPwForm, setProblemPwForm] = useState("")
    
    const [userError, setUserError] = useState("");


    // const noregistableid = [super, admin]
    // const noregistablenickname = [super, admin ]

    const navigate = useNavigate();


    const handleSubmit = async (e) => {
        e.preventDefault();  // prevent the default form submission
        
        // Reset error messages
        setProblemFill("");
        setProblemPw("");
        setProblemPwForm("");
        let isregistable = false;
        let condition1 = false;
        let condition2 = false;
        let condition3 = false;
    
        if (name === "" || password === "" || passwordCheck === "" || email === "") {
            setProblemFill('필드를 다 채워주세요.');
            
        } else {condition1 = true;}
    

        const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&+~])[A-Za-z\d@$!%*#?&+~]{6,20}$/;

        if (!passwordRegex.test(password)) {
            setProblemPwForm('패스워드는 6~20자의 영문자, 숫자, 특수문자를 포함해야 합니다.');
        } else {condition2 = true;}
    
        if (password !== passwordCheck) {
            setProblemPw('패스워드를 다시 체크해주세요.');
        } else {condition3 = true;}

        if (condition1 && condition2 && condition3) {
            isregistable=true;
        }
    

    
        // If there were no problems, then you can submit the form data
        if (isregistable) {

            console.log(JSON.stringify({ name, password, email}))

            try {
                const response = await fetch('http://localhost:8080/member/join', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ name, password, email }),
                });
            
                const data = await response.json();
            
                if (!response.ok) {
                    // 서버가 반환하는 오류 메시지가 있다면 사용하고, 그렇지 않으면 HTTP 상태 코드를 사용합니다.
                    const errorMsg = data.error ? data.error : `HTTP error! status: ${response.status}`;
                    throw new Error(errorMsg);
                }
            
                // 성공적으로 처리된 경우, navigate를 사용하여 로그인 페이지로 이동
                if (data.message) {
                    console.log("아이디 등록완료.");
                    setTimeout(() => {
                        navigate('/login');
                    }, 0);
                }
            
            } catch (error) {
                console.error('Error:', error.message);
                const extractedError = error.message;
                setUserError(extractedError);
            }
            
            
            
        }
    }
    

    

    return (
        <div>
            <div className='join-page'>
                <div className='join-box'>
                    <form name='join-form' onSubmit={handleSubmit}>
                        <p className="join-text">Join our community!</p>
                        {problemFill && <p className='warning'>{problemFill}</p>}
                        {problemPw && <p className='warning'>{problemPw}</p>}
                        {problemPwForm && <p className='warning'>{problemPwForm}</p>}
                        {userError && <p className='warning'>{userError}</p>}
                        <label className="join-email">
                            <p>E-mail</p>
                            <input type="text" name="join-email" value={email} onChange={e => setEmail(e.target.value)}/>
                        </label>
                        <label className="join-name">
                            <p>Nickname</p>
                            <input type="text" name="join-nickname" value={name} onChange={e => setname(e.target.value)}/>
                        </label>
                        <label className="join-pw">
                            <p>Password</p>
                            <input type="password" name="join-password" value={password} onChange={e => setPassword(e.target.value)}/>
                        </label>
                        <label className="join-pw-check">
                            <p>Check Password</p>
                            <input type="password" name="join-password-check" value={passwordCheck} onChange={e => setPasswordCheck(e.target.value)}/>
                        </label>
                        <label className="find-join">
                            {/* <p>find</p> */}
                        </label>
                        <button type="submit">Create</button>
                    </form>
                </div>
            </div>
        </div>
    )
}

export default JoinPage;