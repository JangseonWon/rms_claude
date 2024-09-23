import {Query} from "@/model/Query";

export async function getUsers(search: Query) {
    return await fetch(`/w-api/management-service/users`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(search),
        credentials: 'include',
        cache: 'no-store'
    });
}