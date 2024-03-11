import { Routes, Route, BrowserRouter } from 'react-router-dom';
import { useState, useEffect } from "react";
import './App.css';
import MainPage from './components/js/mainpage';
import NavBar from './components/js/navbar';
import Footer from './components/js/footer';
import LoginPage from './components/js/loginpage';
import MapPage from './components/js/mappage';
import MapSearchHistoryPage from './components/js/mapsearchhistorypage';

const App = () => {

  const urlParams = new URLSearchParams(window.location.search);
  const code = urlParams.get('code');
  const state = urlParams.get('state');

  console.log(code);
  console.log(state);

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
    {/* <NavBar isLoggedIn={isLoggedIn} setIsLoggedIn={setIsLoggedIn} isStaff={isStaff} setIsStaff={setIsStaff} /> */}
      <Routes>
        <Route path="/" element={<MainPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/map" element={<MapPage />} />
        <Route path="/mapsearchhistorypage" element={<MapSearchHistoryPage />} />
      </Routes>
    <Footer />
    </BrowserRouter>
  );
}

export default App;
