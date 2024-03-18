
import React, { useState , useEffect, useRef} from 'react';
import {Wrapper, Status} from "@googlemaps/react-wrapper";
import { useCookies } from 'react-cookie';
import '../css/mappage.css';

const MapPage = () => {
    const [cookies, setCookie, removeCookie] = useCookies(['token']);
    const [keyword, setKeyword] = useState('');
    const [results, setResults] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [addressesPerPage] = useState(5); // 한 페이지당 주소 수
    const mapInstance = useRef(null);
    const [pageSet, setPageSet] = useState(1); // 페이지 세트 관리 (1 세트 당 최대 10개 페이지)


    // 1. 페이지네이션 (주소 목록을 페이지별로 나누어 표시하기 위한 코드)
    // 페이지네이션을 위한 주소 리스트 분할
    const indexOfLastAddress = currentPage * addressesPerPage;
    const indexOfFirstAddress = indexOfLastAddress - addressesPerPage;
    const currentAddresses = results.slice(indexOfFirstAddress, indexOfLastAddress);

    // 전체 페이지 수 계산
    const pageNumbers = [];
    for (let i = 1; i <= Math.ceil(results.length / addressesPerPage); i++) {
        pageNumbers.push(i);
    }

    // 페이지네이션을 위한 페이지 버튼 생성
    const maxPageSet = Math.ceil(pageNumbers.length / 10);
    const startPage = (pageSet - 1) * 10 + 1;
    const endPage = Math.min(startPage + 9, pageNumbers.length);

    // 페이지 세트 관리 함수
    const nextPageSet = () => setPageSet(prev => Math.min(prev + 1, maxPageSet));
    const prevPageSet = () => setPageSet(prev => Math.max(prev - 1, 1));

    // 페이지네이션을 위한 페이지 버튼 렌더링
    const renderPagination = () => (
        <>
            {pageSet > 1 && <button onClick={prevPageSet}>&lt;</button>}
            {pageNumbers.slice(startPage - 1, endPage).map(number => (
                <button key={number} onClick={() => setCurrentPage(number)}>
                    {number}
                </button>
            ))}
            {pageSet < maxPageSet && <button onClick={nextPageSet}>&gt;</button>}
        </>
    );

    // 2. 구글지도 API를 이용한 주소 검색 및 지도 표시
    const render = (status: Status) => {
        return <h1>{status}</h1>; // 로딩 상태에 따라 메시지를 표시
    };

    const MyMapComponent = React.memo(() => {
        const mapRef = useRef(null);
    
        useEffect(() => {
            if (!window.google || !mapRef.current) return;
    
            mapInstance.current = new window.google.maps.Map(mapRef.current, {
                center: { lat: 37.5238862196042, lng: 126.9803237915039 },
                zoom: 13,
            });
        }, []); // 의존성 배열을 비워 컴포넌트 마운트 시 한 번만 실행
    
        return <div ref={mapRef} style={{ width: '100vw', height: '150vh' }} />;
    });

    //3. 주소 검색
    const handleSearch = async () => {
        if (!keyword) {
        alert('Please enter a keyword for search.');
        return;
        }

        const tokenFromLocalStorage = localStorage.getItem('token');
        const tokenFromCookies = cookies.token;

        console.log("tokenFromLocalStorage:", tokenFromLocalStorage)
        console.log("tokenFromCookies:", tokenFromCookies)
        // Determine the token source and provider
        let token, provider;
        if (tokenFromLocalStorage) {
            token = tokenFromLocalStorage;
            provider = "web1"; // Set provider for localStorage token
        } else if (tokenFromCookies) {
            token = tokenFromCookies;
            provider = "google"; // Set provider for cookie token
        }

        if (!token) {
            token = null;
        }

        if (!provider) {
            provider = null;
        }

        console.log(token, provider);

        
        const endPoint = 'http://localhost:8080/map/getaddress';
        const params = {
            currentPage: 1,
            countPerPage: 100,
            resultType: 'json',
            confmKey: 'devU01TX0FVVEgyMDI0MDMwODE0MjIyOTExNDU3Nzc=', // Use the correct API key
            keyword: keyword, // 'keyword' 필드에 사용자 입력 값을 할당
            provider: provider,
        };


        try {
            const response = await fetch(endPoint, {
                method: 'POST',
                headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
                },
                body: JSON.stringify(params)
            });

            console.log(params);

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            console.log(data);

            if (data.results && data.results.common.errorCode !== '0') {
                alert(`${data.results.common.errorCode}=${data.results.common.errorMessage}`);
            } else if (data.results && data.results.juso) {
                setResults(data.results.juso);
            } else {
                alert('No results found.');
            }
        } catch (error) {
    
            console.error('Error occurred while fetching addresses:', error);
            console.log(params);
            alert('Error occurred while fetching addresses.');
            }
        };

    //4. 지오코딩(주소의 좌푯값 변환)을 이용한 주소 검색 및 지도 표시
    const handleSearchMap = async (address) => {
        console.log(address);

        const tokenFromLocalStorage = localStorage.getItem('token');
        const tokenFromCookies = cookies.token;

        console.log("tokenFromLocalStorage:", tokenFromLocalStorage)
        console.log("tokenFromCookies:", tokenFromCookies)
        // Determine the token source and provider
        let token, provider;
        if (tokenFromLocalStorage) {
            token = tokenFromLocalStorage;
            provider = "web1"; // Set provider for localStorage token
        } else if (tokenFromCookies) {
            token = tokenFromCookies;
            provider = "google"; // Set provider for cookie token
        }

        if (!token) {
            token = null;
        }

        if (!provider) {
            provider = null;
        }

        const params = {
            address: address,
            provider: provider,
        };
        
        const response = await fetch('http://localhost:8080/map/getgeocoding', 
        {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            body: JSON.stringify(params),
        });
        const data = await response.json();
        console.log(data);

        data.results.forEach((result) => {
            console.log(result);
            const lat = result.geometry.location.lat;
            const lng = result.geometry.location.lng;
            console.log(lat, lng);
            const latlng = new window.google.maps.LatLng(lat, lng);

            mapInstance.current.panTo(latlng);
            mapInstance.current.setCenter(latlng);
            mapInstance.current.setZoom(15);
            new window.google.maps.Marker({
                position: latlng,
                map: mapInstance.current
            });
        });
    };

    // const handleChange = (e) => {
    //     setKeyword(e.target.value);
    // };

    // const handleKeyPress = (e) => {
    //     if (e.key === 'Enter') {
    //     handleSearch();
    //     }
    // };


    return (
        <div className="map-page">
            <div className="map-container-main-page">
                <Wrapper apiKey={"AIzaSyBGHONO0r5jq-uNwYhQOcQ_IInfj3exhqo"} render={render}>
                    <MyMapComponent/>
                </Wrapper>
            </div>
            <div className="search-results-pagination">
                <div className="search-bar">
                    <input
                        type="text"
                        value={keyword}
                        onChange={e => setKeyword(e.target.value)}
                        onKeyDown={e => e.key === 'Enter' && handleSearch()}
                        placeholder="Search for an address"
                    />
                    <button onClick={handleSearch}>Search</button>
                </div>
                <div className="results-list">
                    {results.length > 0 ? (
                        <ul>
                            {currentAddresses.map((address, index) => (
                                <li key={index} onClick={() => handleSearchMap(address.roadAddr)}>
                                    <div>{address.roadAddr}</div>
                                    <div>{address.jibunAddr}</div>
                                    <div>{address.zipNo}</div>
                                </li>
                            ))}
                        </ul>
                    ) : null}
                </div>
                <div className="pagination">
                    {renderPagination()}
                </div>
            </div>
        </div>
    );
};

export default MapPage;