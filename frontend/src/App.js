import './App.css';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import {LoginPage} from "./pages/LoginPage";
import {HomePage} from "./pages/HomePage";
import {AuthContextProvider} from "./commons/AuthContextProvider";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min";

function App() {
    return (
        <AuthContextProvider>
            <BrowserRouter>
                <Routes>
                    <Route path={"/"} element={<LoginPage/>}/>
                    <Route path={"/login"} element={<LoginPage/>}/>
                    <Route path={"/home"} element={<HomePage />}/>
                </Routes>
            </BrowserRouter>
        </AuthContextProvider>
    );
}

export default App;
