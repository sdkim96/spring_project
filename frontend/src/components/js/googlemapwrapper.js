import React from 'react';
import { Wrapper, Status } from "@googlemaps/react-wrapper";
import MapPage from './mappage';

const render = (status: Status) => {
  return <h1>{status}</h1>; // 로딩 상태에 따라 메시지를 표시
};

const GoogleMapWrapper = () => (
  <Wrapper apiKey={"AIzaSyBGHONO0r5jq-uNwYhQOcQ_IInfj3exhqo"} render={render}>
    <MapPage />
  </Wrapper>
);



export default GoogleMapWrapper;