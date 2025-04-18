import { HttpHeaders } from "@angular/common/http";

export class Utility
{
    static getTokenHeader(): HttpHeaders {
        if (localStorage.getItem("token")) {
            return new HttpHeaders({
                'Authorization': `Bearer ${localStorage.getItem("token")}`,
                'Content-Type': 'application/json'
            });
        } else {
            throw new Error('Token is required');
        }
    }

    static getUserName(): string {
        return localStorage.getItem("username") ?? "";
    }
}