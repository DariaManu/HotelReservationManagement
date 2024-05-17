import {useContext, useEffect, useState} from "react";
import {AuthContext} from "../commons/AuthContextProvider";
import {Card, CardBody, Col, Form, Row} from "react-bootstrap";
import {
    CancelReservation, ChangeReservation, GetFeedbackForHotel,
    GetNearbyHotelsApi,
    GetReservationsForHotelAndUser,
    MakeReservation, PostFeedbackForHotel
} from "../commons/ApiUtils";

export const HomePage = () => {
    const {auth} = useContext(AuthContext);
    const [userPosition, setUserPosition] = useState(() => {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(successFunction, errorFunction);
        } else {
            alert('It seems like Geolocation, which is required for this page, is not enabled in your browser. Please use a browser which supports it.');
        }
    })
    const [radius, setRadius] = useState();
    const [hotels, setHotels] = useState();
    const [rooms, setRooms] = useState();
    const [reservations, setReservations] = useState();
    const [feedback, setFeedback] = useState();

    const [selectedHotel, setSelectedHotel] = useState();
    const [selectedRoom, setSelectedRoom] = useState();
    const [reservationStartDate, setReservationStartDate] = useState();
    const [reservationEndDate, setReservationEndDate] = useState();
    const [selectedReservation, setSelectedReservation] = useState();
    const [userFeedback, setUserFeedback] = useState("");

    function successFunction(position) {
        console.log(position.coords.latitude);
        console.log(position.coords.longitude);
        setUserPosition({userLat: position.coords.latitude, userLon: position.coords.longitude});
    }

    function errorFunction(position) {
        console.log('error');
    }

    useEffect(() => {
        console.log(userPosition);
    }, [userPosition]);

    const getHotelsInRadius = (e) => {
        e.preventDefault();
        if (!radius) {
            return;
        }
        if (!userPosition) {
            alert("user position does not exist!");
            return;
        }
        const data = {radius: radius, userLat: userPosition.userLat, userLon: userPosition.userLon}
        GetNearbyHotelsApi(data)
            .catch((error) => {
                console.log(error);
            })
            .then((response) => {
                if (response && response.status === 200) {
                    setHotels(response.data);
                    console.log(response.data);
                    resetData();
                }
            });
    }

    const resetData = () => {
        setRooms([]);
        setReservations([]);
        setFeedback([]);
    }

    const getRoomsReservationsAndFeedbackForHotel = (e) => {
        e.preventDefault();
        if (!userPosition) {
            alert("User position is not computed");
        }
        const hotelId = parseInt(e.target.id);
        setSelectedHotel(hotelId);
        getRoomsForHotel(hotelId);
        getReservationsForHotelAndUser(hotelId);
        getFeedbackForHotel(hotelId);
    }

    const getRoomsForHotel = (hotelId) => {
        for (const hotel of hotels) {
            if(hotel.id === hotelId) {
                let newRooms = [];
                for (const room of hotel.rooms) {
                    newRooms.push(room);
                }
                setRooms(newRooms);
            }
        }
    }

    const getReservationsForHotelAndUser = (hotelId) => {
        if (!auth) {
            alert("User id is not saved");
            return;
        }
        GetReservationsForHotelAndUser(hotelId, auth.id)
            .catch((err) => {
                console.log(err);
            })
            .then((response) => {
                if (response && response.status === 200) {
                    setReservations(response.data);
                }
            })
    }

    const getFeedbackForHotel = (hotelId) => {
        if (!hotelId) {
            alert("Hotel not selected");
            return;
        }
        GetFeedbackForHotel(hotelId)
            .catch((err) => {
                console.log(err);
            })
            .then((result) => {
                if (result && result.status === 200) {
                    if (result.data) {
                        setFeedback(result.data);
                    }
                }
            })
    }

    const addFeedback = (e) => {
        e.preventDefault();
        if (feedback === "") {
            alert("nothing to post");
            return;
        }
        if (!selectedHotel) {
            alert("No hotel selected");
            return;
        }
        PostFeedbackForHotel(selectedHotel, {feedback: userFeedback})
            .catch((err) => {
                console.log(err);
            })
            .then((response) => {
                if (response && response.status === 200) {
                    setFeedback([...feedback, userFeedback]);
                    setUserFeedback("");
                }
            });
    }

    const bookRoom = (e) => {
        e.preventDefault();
        if (!selectedHotel) {
            alert("Hotel not selected");
            return;
        }
        if (!selectedRoom) {
            alert("Room not selected");
            return;
        }
        if (!reservationStartDate) {
            alert("Start date not selected");
            return;
        }
        if (!reservationEndDate) {
            alert("End date not selected");
            return;
        }
        const data = {
            userId: auth.id,
            hotelId: selectedHotel,
            roomId: selectedRoom,
            startDate: reservationStartDate,
            endDate: reservationEndDate
        }
        console.log(data);
        MakeReservation(data)
            .catch((err) => {
                if (err && err.response.data) {
                    console.log(err.response.data);
                    alert(err.response.data);
                }
            })
            .then((response) => {
                console.log(response);
                getReservationsForHotelAndUser(selectedHotel);
            })
    }

    const cancelReservation = (e) => {
        e.preventDefault();
        if (!selectedReservation) {
            alert("No reservation selected!");
            return;
        }
        CancelReservation(selectedReservation)
            .catch((err) => {
                if (err && err.response.data) {
                    alert(err.response.data);
                }
            })
            .then((result) => {
                if (result && result.status === 200) {
                    console.log(result);
                    getReservationsForHotelAndUser(selectedHotel);
                }
            });
    }

    const modifyReservation = (e) => {
        e.preventDefault();
        if (!selectedReservation) {
            alert("No reservation selected!");
            return;
        }
        if (!selectedRoom) {
            alert("No room selected");
            return;
        }
        ChangeReservation(selectedReservation, selectedRoom)
            .catch((err) => {
                if (err && err.response.data) {
                    alert(err.response.data);
                }
            })
            .then((result) => {
                if (result && result.status === 200) {
                    console.log(result);
                    getReservationsForHotelAndUser(selectedHotel);
                }
            })
    }

    return (
        <div className="container">
            <Card className="container w-50">
                <CardBody>
                    <div className="mb-3"> Enter radius </div>
                    <Form >
                        <Row className="formRow">
                            <Col className="col-md-9">
                                <Form.Control type="number" placeholder="Radius in km" onChange={(e) => setRadius(parseInt(e.target.value))}/>
                            </Col>
                            <Col className="col-md-3">
                                <Form.Control type="submit" value="Get Hotels" className="btn btn-primary" onClick={getHotelsInRadius}/>
                            </Col>
                        </Row>
                    </Form>
                </CardBody>
            </Card>
            <Card className="container w-75 mt-5">
                <CardBody>
                    <Row>
                        <Col className="col-md-4">
                            Hotels
                            <ul className="list-group">
                                {hotels && hotels.map((hotel) => (
                                    <button key={hotel.id} id={hotel.id} type="button" className="list-group-item list-group-item-action" onClick={getRoomsReservationsAndFeedbackForHotel}>{hotel.name}</button>
                                ))}
                            </ul>
                        </Col>
                        <Col className="col-md-4">
                            Rooms
                            <ul className="list-group">
                                {rooms && rooms.map((room) => (
                                    <button key={room.id} id={room.id} type="button" className="list-group-item list-group-item-action" onClick={(e) => setSelectedRoom(parseInt(e.target.id))}>
                                        Nr: {room.roomNumber} - Price: {room.price} - Type: {room.type}
                                    </button>
                                ))}
                            </ul>
                        </Col>
                        <Col className="col-md-4">
                            <Form>
                                <label className="m-2">Start date:</label>
                                <input type="date" id="start" onChange={(e) => {
                                    setReservationStartDate(e.target.value);
                                }}/>
                                <br/>
                                <label className="m-2">End date:</label>
                                <input type="date" id="end" onChange={(e) => {
                                    setReservationEndDate(e.target.value);
                                }}/>
                                <Form.Control type="submit" value="Book selected room" className="btn btn-primary" onClick={bookRoom}/>
                            </Form>
                        </Col>
                    </Row>
                </CardBody>
            </Card>
            <Card className="container w-75 mt-5">
                <CardBody>
                    <Row>
                        <Col className="col-md-4">
                            Feedback from users about this hotel
                            <ul className="list-group">
                                {feedback && feedback.map((val) => (
                                    <li className="list-group-item"> {val} </li>
                                ))}
                            </ul>
                            <br/>
                            Post your feedback
                            <Form className="mt-2">
                                <Form.Control type="text-area" value={userFeedback} onChange={(e) => setUserFeedback(e.target.value)}/>
                                <Form.Control type="submit" value={"Add feedback"} className="btn btn-primary mt-2" onClick={addFeedback}/>
                            </Form>
                        </Col>
                        <Col className="col-md-4">
                            Reservations for selected hotel
                            <ul className="list-group">
                                {reservations && reservations.map((reservation) => (
                                    <button key={reservation.reservationId} id={reservation.reservationId}
                                    type="button" className="list-group-item list-group-item-action"
                                    onClick={(e) => {setSelectedReservation(parseInt(e.target.id))}}>
                                        Hotel: {reservation.hotelName}, Room Number: {reservation.roomNumber}, Room Type: {reservation.roomType}, Period: {reservation.startDate} - {reservation.endDate}
                                    </button>
                                ))}
                            </ul>
                        </Col>
                        <Col className="col-md-4">
                            <Form>
                                <Form.Control type="submit" value={"Cancel selected reservation"} className="btn btn-primary m-3" onClick={cancelReservation}/>
                                <Form.Control type="submit" value={"Change room"} className="btn btn-primary m-3" onClick={modifyReservation}/>
                            </Form>
                        </Col>
                    </Row>
                </CardBody>
            </Card>
        </div>
    )
}