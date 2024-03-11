
import React, { useState , useEffect} from 'react';
import { useRef } from 'react';
import {Wrapper, Status} from "@googlemaps/react-wrapper";

const MapPage = () => {
    const [keyword, setKeyword] = useState('');
    const [results, setResults] = useState([]);
    const mapInstance = useRef(null);

    const render = (status: Status) => {
        return <h1>{status}</h1>; // 로딩 상태에 따라 메시지를 표시
    };

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
    
        return <div ref={mapRef} style={{ width: '100%', height: '400px' }} />;
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
        countPerPage: 10,
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
        <div>
        <form onSubmit={(e) => e.preventDefault()}>
            <input
            type="text"
            name="keyword"
            value={keyword}
            onChange={handleChange}
            onKeyDown={handleKeyPress}
            placeholder="Search for an address"
            />
            <button type="button" onClick={handleSearch}>Search Address</button>
        </form>

        {/* 주소 목록을 동적으로 생성 */}
        <div id="list">
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
                {results.map((addr, index) => (
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
            <Wrapper apiKey={"AIzaSyBGHONO0r5jq-uNwYhQOcQ_IInfj3exhqo"} render={render}>
                <MyMapComponent />
            </Wrapper>
            {results.length === 0 && <p>No results found.</p>}
        </div>
        </div>
    );
};

export default MapPage;