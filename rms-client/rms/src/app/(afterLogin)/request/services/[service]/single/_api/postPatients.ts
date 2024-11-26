import {Query} from "@/model/Query";

export async function postPatients(search: Query) {
    return  await fetch(`/w-api/order-service/patients/search`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(search),
        credentials: 'include',
        cache: 'no-store'
    });
}