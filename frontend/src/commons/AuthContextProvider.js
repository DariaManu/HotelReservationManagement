import {createContext, useEffect, useState} from "react";
import {LoginApi} from "./ApiUtils";

export const AuthContext = createContext();

export const AuthContextProvider = ({children}) => {
    const [auth, setAuth] =  useState(() => {
        const authFromLocalStorage = localStorage.getItem("auth");
        if (authFromLocalStorage) {
            return JSON.parse(authFromLocalStorage);
        }
        return null;
    });

    useEffect(() => {
        if (auth) {
            localStorage.setItem("auth", JSON.stringify(auth));
        }
    }, [auth]);

    const login = ({email, password}) => {
        return new Promise((resolve, reject) => {
            LoginApi({email: email, password: password})
                .catch((error) => {
                    reject(error);
                })
                .then((response) => {
                    if (response && response.status === 200) {
                        const auth = {
                            id: response.data
                        };
                        localStorage.setItem("auth", JSON.stringify(auth));
                        setAuth(auth);
                        resolve();
                    }
                });
        });
    };

    const logout = () => {
        setAuth(null);
        localStorage.removeItem("auth");
    };

    return (
        <AuthContext.Provider value={{auth, setAuth, login, logout}}>
            {children}
        </AuthContext.Provider>
    )
}