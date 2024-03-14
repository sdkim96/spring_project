import { Routes, Route, BrowserRouter } from 'react-router-dom';
import { useState, useEffect } from "react";
import './App.css';
import MainPage from './components/js/mainpage';
import NavBar from './components/js/navbar';
import Footer from './components/js/footer';
import LoginPage from './components/js/loginpage';
import MapPage from './components/js/mappage';
import MapSearchHistoryPage from './components/js/mapsearchhistorypage';
import JoinPage from './components/js/joinpage';
import { useCookies } from 'react-cookie';

const App = () => {

  // const urlParams = new URLSearchParams(window.location.search);
  // const code = urlParams.get('code');
  // const state = urlParams.get('state');

  // console.log(code);
  // console.log(state);

  const [cookies] = useCookies(['token']);
  const tokenFromCookies = cookies.token;

  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    if (tokenFromCookies) {
      console.log("Cookie Token", tokenFromCookies);
      setIsLoggedIn(true);
    } else {
      const tokenFromLocalStorage = localStorage.getItem('token');
      if(tokenFromLocalStorage) {
        setIsLoggedIn(true);
        console.log("Local Storage Token", tokenFromLocalStorage);
      } else {
        setIsLoggedIn(false);
      }
      // setIsLoggedIn(false);
    }
  }, []);

  return (
    <BrowserRouter>
    <NavBar isLoggedIn={isLoggedIn} setIsLoggedIn={setIsLoggedIn} />
      <Routes>
        <Route path="/" element={<MapPage />} />
        <Route path="/login" element={<LoginPage isLoggedIn={isLoggedIn} setIsLoggedIn={setIsLoggedIn} />} />
        <Route path="/join" element={<JoinPage />} />
        <Route path="/mapsearchhistorypage" element={<MapSearchHistoryPage />} />
      </Routes>
    <Footer />
    </BrowserRouter>
  );
}

export default App;
