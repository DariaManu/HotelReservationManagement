import axios from "axios";

export const LoginApi = (data) => {
    return axios.post("http://localhost:8080/backend/api/login", data);
};

export const GetNearbyHotelsApi = (data) => {
    return axios.post("http://localhost:8080/backend/api/hotel", data);
}

export const MakeReservation = (data) => {
    return axios.post("http://localhost:8080/backend/api/hotel/reservation", data);
}

export const GetReservationsForHotelAndUser = (hotelId, userId) => {
    return axios.get(`http://localhost:8080/backend/api/hotel/${hotelId}/reservation/user/${userId}`);
}

export const CancelReservation = (reservationId) => {
    return axios.delete(`http://localhost:8080/backend/api/hotel/reservation/${reservationId}/cancel`);
}

export const ChangeReservation = (reservationId, roomId) => {
    return axios.put(`http://localhost:8080/backend/api/hotel/reservation/${reservationId}/change/${roomId}`);
}

export const GetFeedbackForHotel = (hotelId) => {
    return axios.get(`http://localhost:8080/backend/api/hotel/${hotelId}/feedback`);
}

export const PostFeedbackForHotel = (hotelId, data) => {
    return axios.post(`http://localhost:8080/backend/api/hotel/${hotelId}/feedback`, data);
}