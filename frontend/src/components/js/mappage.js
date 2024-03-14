
import React, { useState , useEffect} from 'react';
import { useRef } from 'react';
import {Wrapper, Status} from "@googlemaps/react-wrapper";
import MyMapComponent from './sources/mymapcomponent';
import '../css/mappage.css';

const MapPage = () => {
    const [keyword, setKeyword] = useState('');
    const [results, setResults] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [addressesPerPage] = useState(13); // 한 페이지당 주소 수
    const mapInstance = useRef(null);

     // 페이지네이션을 위한 주소 리스트 분할
    const indexOfLastAddress = currentPage * addressesPerPage;
    const indexOfFirstAddress = indexOfLastAddress - addressesPerPage;
    const currentAddresses = results.slice(indexOfFirstAddress, indexOfLastAddress);

    // 전체 페이지 수 계산
    const pageNumbers = [];
    for (let i = 1; i <= Math.ceil(results.length / addressesPerPage); i++) {
        pageNumbers.push(i);
    }

    const paginate = (pageNumber) => setCurrentPage(pageNumber);

    const render = (status: Status) => {
        return <h1>{status}</h1>; // 로딩 상태에 따라 메시지를 표시
    };

    // const handleMapLoad = (map) => {
    //     setMapInstance(map);
    // }

    const MyMapComponent = () => {
        const mapRef = useRef(null);

        
        useEffect(() => {
            if (mapRef.current) {
                mapInstance.current = new window.google.maps.Map(mapRef.current, {
                    center: { lat: 37.48, lng: 126.95 },
                    zoom: 8,
                });
            }
        }, []);
    
        return <div ref={mapRef} style={{ width: '100%', height: '600px' }} />;
    };

    const handleSearchMap = async (address) => {
        console.log(address);
        
        const response = await fetch('http://localhost:8080/map/getgeocoding', 
        {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({address: address}),
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

   


    const handleSearch = async () => {
        if (!keyword) {
        alert('Please enter a keyword for search.');
        return;
        }

    const endPoint = 'http://localhost:8080/map/getaddress';
    const params = {
        currentPage: 1,
        countPerPage: 100,
        resultType: 'json',
        confmKey: 'devU01TX0FVVEgyMDI0MDMwODE0MjIyOTExNDU3Nzc=', // Use the correct API key
        keyword: keyword, // 'keyword' 필드에 사용자 입력 값을 할당
      };

    try {
        const response = await fetch(endPoint, {
            method: 'POST',
            headers: {
            'Content-Type': 'application/json',
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

    const handleChange = (e) => {
        setKeyword(e.target.value);
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter') {
        handleSearch();
        }
    };

    return (
        <div className='map-page'>
            <div className='juso-query'>
                <form id='juso-form'onSubmit={(e) => e.preventDefault()}>
                    <input id='juso-input'
                    type="text"
                    name="keyword"
                    value={keyword}
                    onChange={handleChange}
                    onKeyDown={handleKeyPress}
                    placeholder="Search for an address"
                    />
                    <button type="button" id="mappage-button" onClick={handleSearch}>주소 찾기</button>
                </form>
            </div>
            <div className="juso-results">
                <div className='map-container'>
                <Wrapper apiKey={"AIzaSyBGHONO0r5jq-uNwYhQOcQ_IInfj3exhqo"} render={render}>
                    <MyMapComponent/>
                </Wrapper>
                </div>
                <div className='juso-list'>
                    {results.length > 0 && (
                        <table>
                            <thead>
                                <tr>
                                    <th>도로명 주소</th>
                                    <th>지번 주소</th>
                                    <th>우편번호</th>
                                    <th>버튼</th>
                                </tr>
                            </thead>
                            <tbody>
                                {currentAddresses.map((addr, index) => (
                                    <tr key={index}>
                                        <td>{addr.roadAddr}</td>
                                        <td>{addr.jibunAddr}</td>
                                        <td>{addr.zipNo}</td>
                                        <td>
                                            <button
                                            type="button"
                                            onClick={() => {
                                                alert(`You selected the address: ${addr.roadAddr}`);
                                                handleSearchMap(addr.roadAddr);
                                            }}
                                            >
                                            Select
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    )}
                    {results.length === 0 && <p>No results found.</p>}
                    <nav id='juso-pagination'>
                        
                        <ul className='pagination'>
                            {pageNumbers.map(number => (
                                <li key={number} className='page-item'>
                                    <a onClick={() => paginate(number)} className='page-link'>
                                        {number}
                                    </a>
                                </li>
                            ))}
                        </ul>
                    </nav>
                </div>
            </div>
        </div>

    );
};

export default MapPage;