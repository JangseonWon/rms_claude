import {Filter} from "@/model/Filter";

export async function getServicesByUserId(userId: string, search: Filter) {
    return await fetch(`/w-api/management-service/services/users/${userId}`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(search),
        credentials: 'include',
        cache: 'no-store'
    });
}