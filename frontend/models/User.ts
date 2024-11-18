export default class User {
    id: string;
    username: string;
    firstName: string;
    lastName: string;
    email: string;
    emailVerified: boolean;
    createdTimestamp: number;
    enabled: boolean;
    access: {
        manageGroupMembership: boolean;
        view: boolean;
        mapRoles: boolean;
        impersonate: boolean;
        manage: boolean;
    };

    constructor(
        id: string = '',
        username: string = '',
        firstName: string = '',
        lastName: string = '',
        email: string = '',
        emailVerified: boolean = false,
        createdTimestamp: number = 0,
        enabled: boolean = false,
        access: {
            manageGroupMembership: boolean;
            view: boolean;
            mapRoles: boolean;
            impersonate: boolean;
            manage: boolean;
        } = {
            manageGroupMembership: false,
            view: false,
            mapRoles: false,
            impersonate: false,
            manage: false
        }
    ) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.emailVerified = emailVerified;
        this.createdTimestamp = createdTimestamp;
        this.enabled = enabled;
        this.access = access;
    }
}