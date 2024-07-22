import {Service} from "@/model/Service";

export async function deleteUserService(userId: string, service: Service[]) {
    return await fetch(`/w-api/management-service/users/${userId}/services`, {
        method: 'DELETE',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(service),
        credentials: 'include',
        cache: 'no-store'
    });
}