import {Organization} from "@/model/Organization";

export async function putOrganization(userId: string, organization: Organization) {
    const res = await fetch(`/w-api/profile-service/users/${userId}/organizations/${organization.id}`, {
        method: 'PUT',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(organization),
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) {
        const errorMessage = await res.text();
        // alert(`Error: ${errorMessage}`);
    }
    return res
}