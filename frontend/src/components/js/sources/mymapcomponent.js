// // MyMapComponent.js
// import React, { useEffect, useRef } from 'react';
// import { Wrapper, Status } from "@googlemaps/react-wrapper";

// const MyMapComponent = ({ onMapLoad }) => {
//     const mapRef = useRef(null);

//     useEffect(() => {
//         if (mapRef.current) {
//             const map = new window.google.maps.Map(mapRef.current, {
//                 center: { lat: 37.48, lng: 126.95 },
//                 zoom: 8,
//             });
//             onMapLoad(map); // 부모 컴포넌트에 맵 인스턴스 전달
//         }
//     }, [onMapLoad]);

//     return <div ref={mapRef} style={{ width: '100%', height: '600px' }} />;
// };

// export default MyMapComponent;
