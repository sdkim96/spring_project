import { Routes, Route, BrowserRouter } from 'react-router-dom';
import { useState, useEffect } from "react";
import './App.css';
import MainPage from './components/js/mainpage';
import NavBar from './components/js/navbar';
import Footer from './components/js/footer';


const App = () => {

  // 아래 주석문들은 백엔드의 jwt와 staff 알고리즘이 만들어지면 다시 살림

  // const [isLoggedIn, setIsLoggedIn] = useState(false);
  // const [isStaff, setIsStaff] = useState(false);

  // useEffect(() => {
  //   const token = localStorage.getItem('token');
  //   if(token) {
  //     setIsLoggedIn(true);
  //   } else {
  //     setIsLoggedIn(false);
  //   }
  // }, []);

  // useEffect(() => {
  //   const level = Number(localStorage.getItem('level'));
  //   if (level>=2 && isLoggedIn) {
  //     setIsStaff(true);
  //   } else {
  //     setIsStaff(false);
  //   }
  // }, [isLoggedIn]);

  return (
    <BrowserRouter>
    <NavBar />
      <Routes>
        <Route path="/" element={<MainPage />} />
      </Routes>
    <Footer />
    </BrowserRouter>
  );
}

export default App;
