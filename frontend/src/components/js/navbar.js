// import { useState, useEffect } from "react";
// import '../css/navbar.css'
// import { NavLink } from 'react-router-dom';

// const NavBar = ({}) => { // 차후에 isloggedin등등을 파라미터로 추가할 예정

//     const [fade, setFade] = useState(false);
//     const [word, setWord] = useState('Project by sdkim');
//     const [isKorean, setIsKorean] = useState(false);


//     const words = (latin, korean, mouseover) => {
//         if (mouseover) {
//             setFade(true);
//             setTimeout(() => {
//                 setWord(korean);
//                 setFade(false);
//                 setIsKorean(true);  // 한국어 문장 출력 시 상태 업데이트
//             }, 500);
//         } else {
//             setFade(true);
//             setTimeout(() => {
//                 setWord(latin);
//                 setFade(false);
//                 setIsKorean(false);  // 라틴어 문장 출력 시 상태 업데이트
//             }, 500);
//         }
//     }

//     return(
//         <nav>
//             {/* <p 
//                 className={`words ${fade ? 'fade' : ''} ${isKorean ? 'korean' : ''}`}
//                 onMouseOver={() => words('Project by sdkim', 'AI MSP 기술센터', true)}
//                 onMouseOut={() => words('Project by sdkim', 'AI MSP 기술센터', false)}
//             >
//                 {word}
//             </p> */}

//         </nav>
//     )
// }

// export default NavBar;