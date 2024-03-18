import React, { useEffect, useState } from "react";
import { useCookies } from 'react-cookie';
import { useNavigate } from "react-router-dom";
import '../css/mypage.css';
import ProfileUpdateModal from './sources/profileupdatemodal';
import { useRef } from 'react';
import {Wrapper, Status} from "@googlemaps/react-wrapper";

const MyPage = ({isLoggedIn, setIsLoggedIn}) => {
  
    const tokenFromLocalStorage = localStorage.getItem('token');
    const [cookies] = useCookies(['token']);
    const tokenFromCookies = cookies.token;
    const navigate = useNavigate();
    const [queryList, setQueryList] = useState([]);
    const [userProfile, setUserProfile] = useState([]); // [1
    const [organizedQueryList, setOrganizedQueryList] = useState({});
    const [selectedMonth, setSelectedMonth] = useState(null);
    const [showProfileUpdateModal, setShowProfileUpdateModal] = useState(false);
    const [recommendations, setRecommendations] = useState([]);
    const mapInstance = useRef(null);

    const [currentPage, setCurrentPage] = useState(1);
    const [historyPerPage] = useState(10); // 한 페이지 당 히스토리 수

    // 페이지네이션을 위한 로직
    const indexOfLastHistory = currentPage * historyPerPage;
    const indexOfFirstHistory = indexOfLastHistory - historyPerPage;
    const currentHistories = queryList.slice(indexOfFirstHistory, indexOfLastHistory);

    const pageNumbers = [];
    for (let i = 1; i <= Math.ceil(queryList.length / historyPerPage); i++) {
        pageNumbers.push(i);
    }



    // 1. 맵 컴포넌트
    // 맵을 초기화하고, 추천 목록(recommendations)에 기반하여 맵에 마커를 추가합니다. 
    // 이 컴포넌트는 recommendations 배열이 변경될 때마다 재실행됩니다.
    const render = (status: Status) => {
        return <h1>{status}</h1>; // 로딩 상태에 따라 메시지를 표시
    };

    const MyMapComponent = () => {
        const mapRef = useRef(null);

        
        useEffect(() => {
            if (mapRef.current && recommendations.length > 0) {
                const bounds = new window.google.maps.LatLngBounds();
                const map = new window.google.maps.Map(mapRef.current, {
                    center: { lat: 37.5540714, lng: 126.9205868 }, // 기본 중심 좌표를 설정합니다 (예시: 첫 번째 카페 위치)
                    zoom: 15, // 적절한 줌 레벨 설정
                });
    
                // 각 추천 항목에 대해 마커와 인포 윈도우를 생성
                recommendations.forEach((cafe) => {
                    const position = new window.google.maps.LatLng(cafe.latitude, cafe.longitude);
                    bounds.extend(position);
                    const marker = new window.google.maps.Marker({
                        position,
                        map,
                        title: cafe.cafe_name, // 마커 호버 시 카페 이름 표시
                    });
    
                    const infoWindow = new window.google.maps.InfoWindow({
                        content: `<div><p>${cafe.cafe_name}</p></div>`, // 인포 윈도우에 표시될 내용
                    });
    
                    // 마커를 클릭하면 인포 윈도우를 엽니다.
                    marker.addListener("click", () => {
                        infoWindow.open(map, marker);
                    });

                    map.fitBounds(bounds);
                });
            }
        }, [recommendations]);
    
        return <div ref={mapRef} style={{ width: '100%', height: '100%' }} />;
    };


    // 2. 카페 추천 목록 로딩
    // 성공적으로 데이터를 받아오면, setRecommendations을 사용하여 상태를 업데이트합니다.
    const fetchRecommendations = async () => {
        let token = null;
        let provider = null;

        if (tokenFromLocalStorage) {
            token = tokenFromLocalStorage;
            provider = "web1";
        } else if (tokenFromCookies) {
            token = tokenFromCookies;
            provider = "google";
        }

        console.log(token, provider); // react-cookie를 사용하여 쿠키에서 토큰을 가져옵니다.
        if (!token) {
            console.log("토큰 없음");
            navigate('/login');
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/memberpage/recommend', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                }
            });

            if (response.ok) {
                const data = await response.json();
                console.log("추천 목록:", data);
                setRecommendations(data); // 추천 목록 상태 업데이트
            } else {
                console.error("추천 목록 가져오기 실패");
            }
        } catch (error) {
            console.error("네트워크 오류:", error);
        }
    };

    // 컴포넌트가 마운트될 때 추천 목록을 불러옵니다.
    useEffect(() => {
        fetchRecommendations();
    }, []);

    // 3-0. 프로필 업데이트 모달 여닫기 기능
    const toggleProfileUpdateModal = () => setShowProfileUpdateModal(!showProfileUpdateModal);

    // 3-1. 사용자 프로필 정보 서버로부터 가져옵니다. 
    const getUserProfile = async (e) => {
        let token = null;
    
        if (tokenFromLocalStorage) {
            token = tokenFromLocalStorage;
        } else if (tokenFromCookies) {
            token = tokenFromCookies;
        }
    
        if (token) {
            try {
                const response2 = await fetch(`http://localhost:8080/memberpage/userprofile`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        // Optionally, if you need to send provider in headers
                        // 'X-Provider': provider,
                    }
                    // Removed body for GET request
                });
    
                const data = await response2.json();
                if (response2.ok) {
                    console.log("멤버프로필", data);
                    setUserProfile(data);

                } else {   
                    console.log("멤버프로필실패", data);
                }
            } catch (error) {
                console.log(error);
            }
        }
    }
    
    // 3-2 사용자 프로필 정보 업데이트
    const handleProfileUpdateSubmit = async (event) => {
        event.preventDefault();
        const formData = new FormData(event.target);
        
        const updateProfileUrl = 'http://localhost:8080/memberpage/userprofile/update';

        
        let token = null;
        let provider = null;

        if (tokenFromLocalStorage) {
            token = tokenFromLocalStorage;
            provider = "web1";
        } else if (tokenFromCookies) {
            token = tokenFromCookies;
            provider = "google";
        }

        console.log(token, provider);
        
        try {
            const response = await fetch(updateProfileUrl, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
                body: formData,     
            });

            if (response.ok) {
                console.log('Profile updated successfully');
                    
                toggleProfileUpdateModal();
            } else {
                console.error('Failed to update profile');
                    
            }
        } catch (error) {
            console.error('Error submitting form:', error);
                
        }

        console.log('Form submitted', formData);
        toggleProfileUpdateModal(); 
    };


    useEffect(() => {
        const fetchData = async () => {
            if (!(tokenFromLocalStorage || tokenFromCookies)) {
                console.log("토큰없음", tokenFromLocalStorage);
                navigate('/login');
            } else {
                // getUserProfile 함수를 await로 호출하여 응답을 기다립니다.
                await getUserProfile();
                // getUserProfile 함수의 처리가 완료된 후에 getQueryList 함수를 호출합니다.
                getQueryList();
            }
        };
    
        fetchData();
    }, [navigate, tokenFromCookies, tokenFromLocalStorage]);


    // 4-1. 사용자의 검색 기록 관리 
    const getQueryList = async (e) => {
        let token = null;
        let provider = null;
    
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
                const response = await fetch(`http://localhost:8080/memberpage/queryhistory?provider=${provider}`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        
                    }
                });
    
                const data = await response.json();
                if (response.ok) {
                    console.log("쿼리리스트", data);
                    setQueryList(data);

                } else {   
                    console.log("쿼리리스트 실패", data);
                }
            } catch (error) {
                console.log(error);
            }
        }
    }

    
    // 4-2. 쿼리리스트를 받아와서 각 검색 기록을 월별로 구분하여 객체로 정리
    const organizeQueriesByMonth = (queryList) => {
        const queriesByMonth = {};

        queryList.forEach(query => {
            const date = new Date(query.search_date_time);
            // 포맷을 "2024.3" 형식으로 변경합니다.
            const month = `${date.getFullYear()}.${date.getMonth() + 1}`;
            if (!queriesByMonth[month]) {
                queriesByMonth[month] = [];
            }
            queriesByMonth[month].push(query);
        });
    
        Object.keys(queriesByMonth).forEach(month => {
            queriesByMonth[month].sort((a, b) => new Date(a.search_date_time) - new Date(b.search_date_time));
        });
    
        return queriesByMonth;
    
    }

    useEffect(() => {
        if (queryList.length > 0) {
            const organized = organizeQueriesByMonth(queryList);
            setOrganizedQueryList(organized);
            // organizedQueryList의 키(월) 중 하나를 selectedMonth로 설정
            const months = Object.keys(organized);
            if (months.length > 0) {
                setSelectedMonth(months[0]); // 예를 들어, 배열의 첫 번째 월을 선택
            }
        }
    }, [queryList]);
    

  
  
    return (
        <div className="my-page">
            <div className="main-contents">
                <div className="left-section">
                    <div className="profile-section">
                        <img src={`http://localhost:8080${userProfile.photo_path}`} alt="Profile" className="profile-image" />
                            <h2 className="user-name">{userProfile.name}님 안녕하세요!</h2>
                            <p className="user-intro">{userProfile.email}</p>
                            <button onClick={toggleProfileUpdateModal}>Edit Profile</button>
                            {showProfileUpdateModal && (
                                <div className="modal-backdrop">
                                    <div className="modal-content">
                                        <form onSubmit={handleProfileUpdateSubmit}>
                                            <label htmlFor="name">Name:</label>
                                            <input type="text" id="name" name="name" required />
                                            <label htmlFor="photo">Photo:</label>
                                            <input type="file" id="photo" name="photo" />
                                            <button type="submit">Update</button>
                                            <button type="button" onClick={toggleProfileUpdateModal}>Cancel</button>
                                        </form>
                                    </div>
                                </div>
                            )}
                    </div>
                    <div className="map-recommendation-section">
                        
                        <div className="content-wrapper">
                        <img src={`${process.env.PUBLIC_URL}/images/cafe_background.png`} alt="Cafe Background" className="background-image"/>
                            <h1 className="title">이런 카페는 어떠세요?</h1>
                            <div className="map-and-details">
                                <div className="map-container">
                                <Wrapper apiKey={"AIzaSyBGHONO0r5jq-uNwYhQOcQ_IInfj3exhqo"} render={render}>
                                    <MyMapComponent recommendations={recommendations} />
                                </Wrapper>
                                </div>
                                <div className="cafe-details">
                                    <h2 className="subtitle">당신의 관심지역 근처에 이런 카페가 있습니다.</h2>

                                    <ul className="cafe-list">
                                        {/* 각 카페 항목이 여기 들어갑니다. */}
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="right-section">
                    <div className="month-selector">
                        {Object.keys(organizedQueryList).map(month => (
                        <button key={month} onClick={() => setSelectedMonth(month)}>
                            {month}
                        </button>
                        ))}
                    </div>
                    <div className="history-section">
                        <h3 className="section-title">Your History</h3>
                        {selectedMonth && (
                        <div>
                            <h4>{selectedMonth}</h4>
                            <div className="query-list">
                            {organizedQueryList[selectedMonth] && organizedQueryList[selectedMonth]
                                .slice((currentPage - 1) * historyPerPage, currentPage * historyPerPage)
                                .map((query, index) => (
                                <div className="query-item" key={index}>
                                    <p className="query-content">
                                    {new Date(query.search_date_time).toLocaleDateString()} {new Date(query.search_date_time).toLocaleTimeString()} : {query.search_query}
                                    </p>
                                </div>
                            ))}
                            </div>
                        </div>
                        )}
                        <div className="pagination">
                        {pageNumbers.map(number => (
                            <button key={number} onClick={() => setCurrentPage(number)} className={currentPage === number ? 'active' : ''}>
                            {number}
                            </button>
                        ))}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
      
}

export default MyPage;